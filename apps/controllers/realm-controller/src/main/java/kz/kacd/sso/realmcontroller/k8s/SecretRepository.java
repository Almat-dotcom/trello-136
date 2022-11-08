package kz.kacd.sso.realmcontroller.k8s;

import kz.kacd.sso.realmcontroller.k8s.model.K8sResponse;
import kz.kacd.sso.realmcontroller.k8s.model.SecretData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Repository which provides operations on secrets in k8s.
 */
@Slf4j
@RequiredArgsConstructor
public class SecretRepository {

    private final String namespace;
    private final K8sClientFactory factory;

    public K8sResponse<SecretData> find(String secretName) {
        log.debug("Getting secret {} in ns {} ...", secretName, namespace);
        try (var client = factory.create()) {
            var resource = client.secrets().inNamespace(namespace).withName(secretName).get();
            if (resource == null) {
                return K8sResponse.notFound("Secret with name" + secretName + " in ns " + namespace + "not found!");
            }
            return K8sResponse.success(new SecretData(resource.getData()));
        } catch (Exception e) {
            log.error("Error on fetching secret {} in namespace {}!", secretName, namespace, e);
            return K8sResponse.internalError(e.getMessage());
        }
    }
}
