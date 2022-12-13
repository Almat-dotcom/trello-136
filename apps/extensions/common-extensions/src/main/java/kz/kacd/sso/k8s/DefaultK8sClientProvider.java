package kz.kacd.sso.k8s;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import org.jboss.logging.Logger;

public class DefaultK8sClientProvider implements K8sClientProvider {
    private static final Logger log = Logger.getLogger(DefaultK8sClientProvider.class);

    private final KubernetesClient client;

    public DefaultK8sClientProvider() {
        log.debug("Creating new kubernetes client ...");
        client = new KubernetesClientBuilder().build();
    }

    @Override
    public KubernetesClient getClient() {
        return client;
    }

    @Override
    public void close() {
        // Nothing to close
    }
}
