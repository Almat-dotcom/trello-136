package kz.kacd.sso.realmcontroller.keycloak;

import kz.kacd.sso.realmcontroller.k8s.model.KeycloakRealm;
import kz.kacd.sso.realmcontroller.k8s.model.SecretData;
import kz.kacd.sso.realmcontroller.keycloak.model.Realm;
import kz.kacd.sso.realmcontroller.keycloak.model.RealmBuilder;
import kz.kacd.sso.realmcontroller.model.OperationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class KeycloakRealmController {

    private final KeycloakRealmRepository repository;

    public OperationResponse<Boolean> apply(KeycloakRealm realm, Map<String, SecretData> secrets) {
        log.info("Applying realm {} into keycloak ...", realm.getName());
        var existingRes = repository.find(realm.getName());
        if (realm.action() == KeycloakRealm.RealmAction.DELETED) {
            return delete(existingRes);
        } else if (
                realm.action() == KeycloakRealm.RealmAction.ADDED
                        || realm.action() == KeycloakRealm.RealmAction.MODIFIED
        ) {
            return merge(existingRes, realm, secrets);
        } else {
            return OperationResponse.success(true);
        }
    }

    private OperationResponse<Boolean> merge(
            OperationResponse<Realm> existingRes,
            KeycloakRealm realm, Map<String,
            SecretData> secrets
    ) {
        RealmBuilder keycloakRealm;
        if (existingRes instanceof OperationResponse.Success<Realm> s) {
            keycloakRealm = s.getData().update();
        } else if (((OperationResponse.Failure<?>) existingRes).getKind() == OperationResponse.K8sFailures.NOT_FOUND) {
            keycloakRealm = Realm.newInstance(realm.getName());
        } else {
            return existingRes.map(it -> true);
        }
        return repository.save(keycloakRealm.buildFrom(realm, secrets)).map(it -> true);
    }

    private OperationResponse<Boolean> delete(OperationResponse<Realm> existingRes) {
        if (existingRes instanceof OperationResponse.Success<Realm> s) {
            if (!s.getData().getName().equals("master")) {
                return repository.delete(s.getData());
            }
        }
        return OperationResponse.success(true);
    }
}
