package kz.kacd.sso.realmcontroller.k8s;

import io.fabric8.kubernetes.client.Watcher;
import io.fabric8.kubernetes.client.WatcherException;
import kz.kacd.sso.realmcontroller.k8s.crd.client.Client;
import kz.kacd.sso.realmcontroller.k8s.crd.client.ClientList;
import kz.kacd.sso.realmcontroller.k8s.model.K8sAction;
import kz.kacd.sso.realmcontroller.k8s.model.K8sClient;
import kz.kacd.sso.realmcontroller.model.OperationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class ClientRepository {

    private final String namespace;
    private final K8sClientFactory factory;

    public OperationResponse<Boolean> save(K8sClient entity) {
        log.debug("Saving client {} in ns {} ...", entity.name(), namespace);
        try (var k8s = factory.create()) {
            var existing = list();
            if (existing instanceof OperationResponse.Success<List<K8sClient>> s) {
                var client = s.getData().stream().filter(it -> it.name().equals(entity.name()))
                        .findFirst()
                        .orElseThrow();

                k8s.resource(
                        new K8sClient(client.source(), entity.newStatus(), entity.secrets(), entity.action())
                                .updatedResource()
                ).patchStatus();
                return OperationResponse.success(true);
            }
            return OperationResponse.notFound("Client " + entity.name() + " in ns " + namespace + " not found!");
        } catch (Exception e) {
            log.error("Error on saving client {} in ns {}!", entity.name(), namespace, e);
            return OperationResponse.internalError(e.getMessage());
        }
    }

    public OperationResponse<List<K8sClient>> list() {
        log.debug("Listing all clients in ns {} ...", namespace);
        try (var client = factory.create()) {
            var resource = client.resources(Client.class, ClientList.class)
                    .list();
            if (resource.getItems().isEmpty()) {
                return OperationResponse.notFound("Clients in ns " + namespace + " not found!");
            }
            return OperationResponse.success(
                    resource.getItems().stream()
                            .map(it -> new K8sClient(it, null, null, K8sAction.MODIFIED))
                            .toList()
            );
        } catch (Exception e) {
            log.error("Error on listing all clients in ns {}!", namespace, e);
            return OperationResponse.internalError(e.getMessage());
        }
    }

    public Flux<K8sClient> watch() {
        log.debug("Watching clients in ns {} ...", namespace);
        var client = factory.create();
        return Flux.<K8sClient>create(sink -> {
            try {
                log.debug("Opening watching connection ...");
                client.resources(Client.class, ClientList.class).watch(new Watcher<Client>() {
                    @Override
                    public void eventReceived(Action action, Client resource) {
                        sink.next(new K8sClient(resource, null, null, K8sAction.valueOf(action.name())));
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
                log.error("Error on watching clients in ns {}!", namespace, e);
                sink.error(e);
            }
        }).doOnCancel(client::close).doOnComplete(client::close);
    }
}
