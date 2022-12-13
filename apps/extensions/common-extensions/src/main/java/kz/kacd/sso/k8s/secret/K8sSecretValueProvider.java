package kz.kacd.sso.k8s.secret;

import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.jboss.logging.Logger;

import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.Base64;
import java.util.Map;
import java.util.stream.Collectors;

public class K8sSecretValueProvider implements SecretValueProvider {
    private static final Logger log = Logger.getLogger(K8sSecretValueProvider.class);

    private final KubernetesClient client;

    public K8sSecretValueProvider(KubernetesClient client) {
        this.client = client;
    }

    @Override
    public Secret findByName(String name) {
        log.debugf("Finding secret by name %s ...", name);
        io.fabric8.kubernetes.api.model.Secret result = client.secrets().withName(name).get();
        if (result == null) {
            log.debugf("Secret with name %s not found!", name);
            return null;
        }

        return new SecretAdapter(result);
    }

    @Override
    public Secret create(String name, Map<String, String> labels, Map<String, String> data) {
        log.debugf("Creating new secret %s ...", name);

        ObjectMeta meta = new ObjectMeta();
        meta.setName(name);
        meta.setLabels(labels);

        Map<String, String> targetData = data.entrySet().stream().map(
                it -> new AbstractMap.SimpleEntry<>(
                        it.getKey(),
                        Base64.getEncoder().encodeToString(it.getValue().getBytes(StandardCharsets.UTF_8))
                )
        ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        io.fabric8.kubernetes.api.model.Secret secret = new io.fabric8.kubernetes.api.model.Secret();
        secret.setApiVersion("v1");
        secret.setKind("Secret");
        secret.setMetadata(meta);
        secret.setData(targetData);

        secret = client.resource(secret).createOrReplace();
        return new SecretAdapter(secret);
    }

    @Override
    public void close() {
        // Nothing to close
    }
}
