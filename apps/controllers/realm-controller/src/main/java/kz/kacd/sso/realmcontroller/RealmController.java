package kz.kacd.sso.realmcontroller;

import kz.kacd.sso.realmcontroller.k8s.K8sClientController;
import kz.kacd.sso.realmcontroller.k8s.K8sRealmController;
import kz.kacd.sso.realmcontroller.k8s.model.K8sAction;
import kz.kacd.sso.realmcontroller.k8s.model.K8sRealm;
import kz.kacd.sso.realmcontroller.keycloak.realm.KeycloakRealmController;
import kz.kacd.sso.realmcontroller.model.OperationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.scheduler.Schedulers;

import javax.annotation.PostConstruct;

@Service
@Slf4j
@RequiredArgsConstructor
public class RealmController {

    private final K8sRealmController k8sRealms;
    private final K8sClientController k8sClients;
    private final KeycloakRealmController keycloak;

    @PostConstruct
    public void startWatch() {
        log.info("Checking config for all realms ...");
        k8sRealms.all().forEach(this::onNext);
        log.info("Starting watch of realms ...");
        k8sRealms.watch().subscribeOn(Schedulers.single())
                .subscribe(
                        this::onNext,
                        this::onError,
                        this::onComplete
                );
    }

    private void onNext(K8sRealm realm) {
        if (realm.e() != null) {
            k8sRealms.save(realm.realm().detected().applying().failed(realm.e()));
        }

        if (realm.realm().action() == K8sAction.DELETED) {
            var res = keycloak.apply(realm.realm(), realm.secrets());
            if (res instanceof OperationResponse.Failure<Boolean> f) {
                log.warn("Cannot delete realm: {}", f.getMessage());
            }
            return;
        }

        var currentRealm = realm.realm().detected();
        k8sRealms.save(currentRealm);
        currentRealm = currentRealm.applying();
        k8sRealms.save(currentRealm);
        var res = keycloak.apply(realm.realm(), realm.secrets());
        if (res instanceof OperationResponse.Success<Boolean>) {
            k8sRealms.save(currentRealm.applied());
        } else {
            var f = (OperationResponse.Failure<?>) res;
            k8sRealms.save(currentRealm.failed(f.getKind() + ": " + f.getMessage()));
        }
    }

    private void onError(Throwable e) {
        log.error("Error on watching realms!", e);
    }

    private void onComplete() {
        log.warn("Watching realms stopped!");
    }
}
