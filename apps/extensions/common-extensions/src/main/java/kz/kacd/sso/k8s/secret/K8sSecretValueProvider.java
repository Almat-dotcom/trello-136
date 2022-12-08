package kz.kacd.sso.k8s.secret;

import io.fabric8.kubernetes.client.KubernetesClient;
import org.jboss.logging.Logger;

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
    public void close() {
        // Nothing to close
    }
}
