package kz.kacd.sso.realmcontroller;

import kz.kacd.sso.realmcontroller.k8s.K8sClientController;
import kz.kacd.sso.realmcontroller.k8s.crd.client.model.ClientStatus;
import kz.kacd.sso.realmcontroller.k8s.model.K8sAction;
import kz.kacd.sso.realmcontroller.k8s.model.K8sClient;
import kz.kacd.sso.realmcontroller.keycloak.realm.KeycloakRealmController;
import kz.kacd.sso.realmcontroller.model.OperationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.scheduler.Schedulers;

import javax.annotation.PostConstruct;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClientController {

    private final K8sClientController k8s;
    private final KeycloakRealmController keycloak;

    /**
     * Checks and restarts all clients with backoff or waiting for realm.
     * </>
     * All clients with backoff should be rerun after backoff period past.
     * All clients with state waiting for realm should be rerun every time
     * when job has started.
     */
    @Scheduled(initialDelay = 600_000, fixedDelay = 600_000)
    public void checkBackoffed() {
        log.info("Listing all clients with backoff ...");
        k8s.all().stream().filter(it -> it instanceof OperationResponse.Success<K8sClient>)
                .map(it -> ((OperationResponse.Success<K8sClient>) it).getData())
                .filter(it ->
                        it.newStatus() != null
                                && (it.newStatus().getState() == ClientStatus.State.BACKOFF
                                        || it.newStatus().getState() == ClientStatus.State.WAITING_FOR_REALM)
                )
                .map(OperationResponse::success)
                .forEach(this::onNext);
    }

    /**
     * Lists initial clients and applies all to their realms.
     * And initializes watching on client changes.
     */
    @PostConstruct
    public void start() {
        log.info("Listing clients in k8s ...");
        k8s.all().forEach(this::onNext);
        log.info("Watching client changes ...");
        k8s.watch().subscribeOn(Schedulers.newSingle("client-controller"))
                .subscribe(
                        this::onNext,
                        this::onError,
                        this::onComplete
                );
    }

    private void onNext(OperationResponse<K8sClient> response) {
        // Reject applying if client has been fetched with failure.
        if (response instanceof OperationResponse.Failure<K8sClient> f) {
            if (f.getData() == null) {
                return;
            }
            log.info("Failing client {} with message {}!", f.getData().name(), f.getMessage());
            k8s.save(f.getData().fail(f.getMessage()));
            return;
        }
        assert response instanceof OperationResponse.Success<K8sClient>;
        var client = ((OperationResponse.Success<K8sClient>) response).getData();

        // Skip clients which has unfinished backoff period.
        if (!client.backoffPast()) {
            return;
        }

        // Just deleting existing client without state changes.
        if (client.action() == K8sAction.DELETED) {
            log.info("Deleting client {} ...", client.name());
            keycloak.applyClient(client);
            return;
        }

        log.info("Applying client {} ...", client.name());
        k8s.save(client.apply());

        var applyingResult = keycloak.applyClient(client);
        // If realm not found forcing clients to wait until realm created.
        if (
                applyingResult instanceof OperationResponse.Failure<Boolean> f
                        && f.getKind() == OperationResponse.K8sFailures.NOT_FOUND
        ) {
            log.warn("Realm {} not found! Cannot apply client {} to the realm!", client.realmName(), client.name());
            k8s.save(client.waitRealm(f.getMessage()));
            return;
        }
        // If error we get one chance to retry applying.
        if (applyingResult instanceof OperationResponse.Failure<Boolean> f) {
            log.warn("Client {} applying failed: {}", client.name(), f.getMessage());
            if (client.newStatus().getState() == ClientStatus.State.BACKOFF) {
                k8s.save(client.fail(f.getMessage()));
            } else {
                k8s.save(client.backoff(f.getMessage()));
            }
            return;
        }
        // YUY! It's done.
        if (applyingResult instanceof OperationResponse.Success<Boolean> s) {
            log.info("Successfully applied client {}.", client.name());
            k8s.save(client.success());
        }
    }

    private void onError(Throwable e) {
        log.error("Error on watching clients!", e);
    }

    private void onComplete() {
        log.warn("Watching clients stopped!");
    }
}
