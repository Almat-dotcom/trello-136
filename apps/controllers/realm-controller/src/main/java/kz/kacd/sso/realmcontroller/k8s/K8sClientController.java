package kz.kacd.sso.realmcontroller.k8s;

import kz.kacd.sso.realmcontroller.k8s.model.K8sAction;
import kz.kacd.sso.realmcontroller.k8s.model.K8sClient;
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
public class K8sClientController {

    private final ClientRepository repository;
    private final SecretRepository secrets;

    public Flux<OperationResponse<K8sClient>> watch() {
        log.info("Watching all clients in kubernetes ...");
        return repository.watch()
                .filter(K8sClient::toApply)
                .map(it -> {
                    if (it.action() == K8sAction.DELETED) {
                        return OperationResponse.success(it);
                    }

                    try {
                        var secrets = findSecrets(it);
                        return OperationResponse.success(it.withSecrets(secrets));
                    } catch (Exception e) {
                        return OperationResponse.internalError(e.getMessage(), it);
                    }
                });
    }

    public List<OperationResponse<K8sClient>> all() {
        log.info("Listing all clients ...");
        var result = repository.list();
        if (result instanceof OperationResponse.Success<List<K8sClient>> s) {
            return s.getData().stream().map(it -> {
                try {
                    var secrets = findSecrets(it);
                    return OperationResponse.success(it.withSecrets(secrets));
                } catch (Exception e) {
                    return OperationResponse.<K8sClient>internalError(e.getMessage(), it);
                }
            }).toList();
        } else if (result instanceof OperationResponse.Failure<?> f && f.getKind() == OperationResponse.K8sFailures.NOT_FOUND) {
            log.info("No clients found!");
            return List.of();
        } else {
            assert result instanceof OperationResponse.Failure<?>;
            throw new RuntimeException(((OperationResponse.Failure<?>) result).getMessage());
        }
    }

    private Map<String, SecretData> findSecrets(K8sClient client) {
        var result = new HashMap<String, SecretData>();
        if (
                client.spec() != null
                        && client.spec().getCapability() != null
                        && client.spec().getCapability().getClientExistingSecret() != null
        ) {
            var secret = secrets.find(client.spec().getCapability().getClientExistingSecret());
            if (secret instanceof OperationResponse.Success<SecretData> s) {
                result.put(client.spec().getCapability().getClientExistingSecret(), s.getData());
            } else {
                throw new RuntimeException(((OperationResponse.Failure<?>) secret).getMessage());
            }
        }
        return result;
    }

    public void save(K8sClient realm) {
        var res = repository.save(realm);
        if (res instanceof OperationResponse.Failure<Boolean> f) {
            log.info("Error on saving client: {}: {}!", f.getKind(), f.getMessage());
        }
    }
}
