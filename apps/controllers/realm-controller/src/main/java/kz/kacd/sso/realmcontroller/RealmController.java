package kz.kacd.sso.realmcontroller;

import kz.kacd.sso.realmcontroller.k8s.K8sController;
import kz.kacd.sso.realmcontroller.k8s.model.K8sRealm;
import kz.kacd.sso.realmcontroller.k8s.model.KeycloakRealm;
import kz.kacd.sso.realmcontroller.keycloak.KeycloakRealmController;
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

    private final K8sController k8s;
    private final KeycloakRealmController keycloak;

    @PostConstruct
    public void startWatch() {
        log.info("Checking config for all realms ...");
        k8s.all().forEach(this::onNext);
        log.info("Starting watch of realms ...");
        k8s.watch().subscribeOn(Schedulers.single())
                .subscribe(
                        this::onNext,
                        this::onError,
                        this::onComplete
                );
    }

    private void onNext(K8sRealm realm) {
        if (realm.realm().action() == KeycloakRealm.RealmAction.DELETED) {
            var res = keycloak.apply(realm.realm(), realm.secrets());
            if (res instanceof OperationResponse.Failure<Boolean> f) {
                log.warn("Cannot delete realm: {}", f.getMessage());
            }
            return;
        }

        var currentRealm = realm.realm().detected();
        k8s.save(currentRealm);
        currentRealm = currentRealm.applying();
        k8s.save(currentRealm);
        var res = keycloak.apply(realm.realm(), realm.secrets());
        if (res instanceof OperationResponse.Success<Boolean>) {
            k8s.save(currentRealm.applied());
        } else {
            var f = (OperationResponse.Failure<?>) res;
            k8s.save(currentRealm.failed(f.getKind() + ": " + f.getMessage()));
        }
    }

    private void onError(Throwable e) {
        log.error("Error on watching realms!", e);
    }

    private void onComplete() {
        log.warn("Watching realms stopped!");
    }
}
