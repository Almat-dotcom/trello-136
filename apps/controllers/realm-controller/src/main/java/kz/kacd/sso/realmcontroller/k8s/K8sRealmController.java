package kz.kacd.sso.realmcontroller.k8s;

import kz.kacd.sso.realmcontroller.k8s.model.K8sAction;
import kz.kacd.sso.realmcontroller.k8s.model.K8sRealm;
import kz.kacd.sso.realmcontroller.k8s.model.KeycloakRealm;
import kz.kacd.sso.realmcontroller.k8s.model.SecretData;
import kz.kacd.sso.realmcontroller.model.OperationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class K8sRealmController {

    private final RealmRepository realms;
    private final SecretRepository secrets;

    public Flux<K8sRealm> watch() {
        log.info("Watching all realms inside kubernetes ...");
        return realms.watch()
                .filter(KeycloakRealm::toApply)
                .map(realm -> {
                    if (realm.action() == K8sAction.DELETED) {
                        return new K8sRealm(realm, new HashMap<>(), null);
                    }

                    try {
                        var secrets = findSecrets(realm);
                        return new K8sRealm(realm, secrets, null);
                    } catch (Exception e) {
                        return new K8sRealm(realm, null, e);
                    }
                });
    }

    public List<K8sRealm> all() {
        log.debug("Listing all realms ...");
        var result = realms.list();
        if (result instanceof OperationResponse.Success<List<KeycloakRealm>> s) {
            return s.getData().stream()
                    .map(realm -> {
                        try {
                            var secrets = findSecrets(realm);
                            return new K8sRealm(realm, secrets, null);
                        } catch (Exception e) {
                            return new K8sRealm(realm, null, e);
                        }
                    })
                    .toList();
        } else if (result instanceof OperationResponse.Failure<List<KeycloakRealm>> f) {
            if (f.getKind() == OperationResponse.K8sFailures.NOT_FOUND) {
                log.info("No realms found.");
                return List.of();
            }
            throw new RuntimeException("Failed to fetch realms: " + f.getMessage());
        }

        throw new RuntimeException("Invalid state! It should not happen!");
    }

    private Map<String, SecretData> findSecrets(KeycloakRealm realm) {
        var result = new HashMap<String, SecretData>();
        if (realm.source().getSpec().getEmail().getAuthentication() != null) {
            var secret = realm.source().getSpec().getEmail().getAuthentication().getExistingSecret();
            putSecret(result, secret);
        }
        if (
                realm.source().getSpec().getFederations() != null
                        && realm.source().getSpec().getFederations().getLdap() != null
                        && realm.source().getSpec().getFederations().getLdap().getConnection().getAuth() != null
        ) {
            var secret = realm.source()
                    .getSpec()
                    .getFederations()
                    .getLdap()
                    .getConnection()
                    .getAuth()
                    .getExistingSecret();
            putSecret(result, secret);
        }
        return result;
    }

    private void putSecret(Map<String, SecretData> target, String name) {
        var data = secrets.find(name);
        if (data instanceof OperationResponse.Success<SecretData> s) {
            target.put(name, s.getData());
        } else {
            throw new IllegalArgumentException("Invalid existing secret name: " + name + "!");
        }
    }

    public void save(KeycloakRealm realm) {
        var res = realms.save(realm);
        if (res instanceof OperationResponse.Failure<Boolean> f) {
            log.info("Error on saving realm: {}: {}!", f.getKind(), f.getMessage());
        }
    }
}
