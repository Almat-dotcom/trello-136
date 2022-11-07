package kz.kacd.sso.realmcontroller.k8s;

import kz.kacd.sso.realmcontroller.k8s.crd.Realm;
import kz.kacd.sso.realmcontroller.k8s.crd.RealmList;
import kz.kacd.sso.realmcontroller.k8s.model.K8sResponse;
import kz.kacd.sso.realmcontroller.k8s.model.KeycloakRealm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository which provides operation on realms in k8s.
 */
@Repository
@Slf4j
@RequiredArgsConstructor
public class RealmRepository {

    private final String namespace;
    private final K8sClientFactory factory;

    public K8sResponse<List<KeycloakRealm>> list() {
        log.debug("Listing all realms in ns {} ...", namespace);
        try (var client = factory.create()) {
            var resource = client.resources(Realm.class, RealmList.class)
                    .inNamespace(namespace)
                    .list();
            if (resource.getItems().isEmpty()) {
                return K8sResponse.notFound("Realms in ns " + namespace + " not found!");
            }
            return K8sResponse.success(
                    resource.getItems().stream().map(KeycloakRealm::new).toList()
            );
        } catch (Exception e) {
            log.error("Error on fetching all realms in ns {}!", namespace, e);
            return K8sResponse.internalError(e.getMessage());
        }
    }
}
