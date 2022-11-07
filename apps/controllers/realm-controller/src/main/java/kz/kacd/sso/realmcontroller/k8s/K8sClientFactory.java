package kz.kacd.sso.realmcontroller.k8s;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import org.springframework.stereotype.Component;

/**
 * Factory to create k8s client.
 * </>
 * This factory uses to create additional abstraction between project code and
 * fabric0 library.
 */
@Component
public class K8sClientFactory {

    public KubernetesClient create() {
        return new KubernetesClientBuilder().build();
    }
}
