package kz.kacd.sso.realmcontroller.keycloak.realm;

import kz.kacd.sso.realmcontroller.k8s.model.K8sAction;
import kz.kacd.sso.realmcontroller.k8s.model.K8sClient;
import kz.kacd.sso.realmcontroller.k8s.model.KeycloakRealm;
import kz.kacd.sso.realmcontroller.k8s.model.SecretData;
import kz.kacd.sso.realmcontroller.keycloak.client.KeycloakClientController;
import kz.kacd.sso.realmcontroller.keycloak.component.KeycloakFederationController;
import kz.kacd.sso.realmcontroller.keycloak.flow.KeycloakFlowController;
import kz.kacd.sso.realmcontroller.keycloak.Realm;
import kz.kacd.sso.realmcontroller.keycloak.realm.model.RealmBuilder;
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
    private final KeycloakFederationController federations;
    private final KeycloakFlowController flows;
    private final KeycloakClientController clients;

    public OperationResponse<Boolean> apply(KeycloakRealm realm, Map<String, SecretData> secrets) {
        log.info("Applying realm {} into keycloak ...", realm.getName());
        var existingRes = repository.find(realm.getName());
        if (realm.action() == K8sAction.DELETED) {
            return delete(existingRes);
        } else if (
                realm.action() == K8sAction.ADDED
                        || realm.action() == K8sAction.MODIFIED
        ) {
            return merge(existingRes, realm, secrets);
        } else {
            return OperationResponse.success(true);
        }
    }

    private OperationResponse<Boolean> merge(
            OperationResponse<Realm> existingRes,
            KeycloakRealm realm,
            Map<String, SecretData> secrets
    ) {
        RealmBuilder keycloakRealm;
        if (existingRes instanceof OperationResponse.Success<Realm> s) {
            keycloakRealm = s.getData().update();
        } else if (((OperationResponse.Failure<?>) existingRes).getKind() == OperationResponse.K8sFailures.NOT_FOUND) {
            keycloakRealm = Realm.newInstance(realm.getName());
        } else {
            return existingRes.map(it -> true);
        }

        var result = repository.save(keycloakRealm.buildFrom(realm, secrets));
        if (result instanceof OperationResponse.Failure) {
            return result.map(it -> true);
        }
        var feds = federations.apply(
                ((OperationResponse.Success<Realm>) result).getData(),
                realm.source().getSpec().getFederations(),
                secrets
        );
        if (feds instanceof OperationResponse.Failure) {
            return feds.map(it -> true);
        }
        return flows.createRestrictedFlow(((OperationResponse.Success<Realm>) result).getData());
    }

    private OperationResponse<Boolean> delete(OperationResponse<Realm> existingRes) {
        if (existingRes instanceof OperationResponse.Success<Realm> s) {
            if (!s.getData().name().equals("master")) {
                return repository.delete(s.getData());
            }
        }
        return OperationResponse.success(true);
    }

    public OperationResponse<Boolean> applyClient(K8sClient client) {
        var realm = repository.find(client.realmName());
        if (realm instanceof OperationResponse.Success<Realm> s) {
            var result = clients.apply(s.getData(), client).map(it -> true);
            if (result instanceof OperationResponse.Failure<Boolean> f) {
                return OperationResponse.internalError(f.getMessage());
            }
            return result;
        } else {
            return realm.map(it -> true);
        }
    }
}
