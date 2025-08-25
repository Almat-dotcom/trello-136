package kz.kacd.sso.k8s;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import org.jboss.logging.Logger;

public class K8sClientProvider {
    private static final Logger log = Logger.getLogger(K8sClientProvider.class);
    
    private static KubernetesClient client;
    
    public static synchronized KubernetesClient getClient() {
        if (client == null) {
            log.info("Creating Kubernetes client...");
            client = new KubernetesClientBuilder().build();
        }
        return client;
    }
    
    public static void closeClient() {
        if (client != null) {
            log.info("Closing Kubernetes client...");
            client.close();
            client = null;
        }
    }
}
