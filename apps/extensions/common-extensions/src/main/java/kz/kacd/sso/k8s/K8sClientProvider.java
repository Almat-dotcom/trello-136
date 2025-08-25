package kz.kacd.sso.k8s;

import io.fabric8.kubernetes.client.KubernetesClient;
import org.keycloak.provider.Provider;

public interface K8sClientProvider extends Provider {

    KubernetesClient getClient();
}
