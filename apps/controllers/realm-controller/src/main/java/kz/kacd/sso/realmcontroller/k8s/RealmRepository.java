package kz.kacd.sso.realmcontroller.k8s;

import io.fabric8.kubernetes.api.model.GenericKubernetesResourceList;
import io.fabric8.kubernetes.client.Watcher;
import io.fabric8.kubernetes.client.WatcherException;
import kz.kacd.sso.realmcontroller.k8s.crd.Realm;
import kz.kacd.sso.realmcontroller.k8s.crd.RealmList;
import kz.kacd.sso.realmcontroller.k8s.model.K8sResponse;
import kz.kacd.sso.realmcontroller.k8s.model.KeycloakRealm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * Repository which provides operation on realms in k8s.
 */
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
                    resource.getItems().stream()
                            .map(it -> new KeycloakRealm(it, null, KeycloakRealm.RealmAction.MODIFIED))
                            .toList()
            );
        } catch (Exception e) {
            log.error("Error on fetching all realms in ns {}!", namespace, e);
            return K8sResponse.internalError(e.getMessage());
        }
    }

    public Flux<KeycloakRealm> watch() {
        log.debug("Creating watching realm resources in ns {} ...", namespace);
        var client = factory.create();
        return Flux.<KeycloakRealm>create(sink -> {
            try {
                log.debug("Opening watching connection ...");
                client.resources(Realm.class, RealmList.class).watch(new Watcher<>() {
                    @Override
                    public void eventReceived(Action action, Realm resource) {
                        sink.next(new KeycloakRealm(resource, null, KeycloakRealm.RealmAction.from(action)));
                    }

                    @Override
                    public void onClose(WatcherException cause) {
                        if (cause != null) {
                            sink.error(cause);
                        } else {
                            sink.complete();
                        }
                    }
                });
            } catch (Exception e) {
                log.error("Error on watching realms!", e);
                sink.error(e);
            }
        }).doOnCancel(client::close);
    }

    public K8sResponse<Boolean> save(KeycloakRealm realm) {
        log.debug("Saving realm {} in ns {}", realm.getName(), namespace);
        try (var client = factory.create()) {
            client.resource(realm.updatedResource()).patchStatus();
            return K8sResponse.success(true);
        } catch (Exception e) {
            log.error("Error on editing realm state {} in ns {}!", realm.getName(), namespace, e);
            return K8sResponse.internalError(e.getMessage());
        }
    }
}
