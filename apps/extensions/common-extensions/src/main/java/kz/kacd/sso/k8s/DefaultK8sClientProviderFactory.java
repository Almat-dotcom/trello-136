package kz.kacd.sso.k8s;

import com.google.auto.service.AutoService;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

@AutoService(K8sClientProviderFactory.class)
public class DefaultK8sClientProviderFactory implements K8sClientProviderFactory {
    private static final String PROVIDER_ID = "default-k8s-client-provider";

    @Override
    public K8sClientProvider create(KeycloakSession session) {
        // Lazily construct the Kubernetes client at runtime to avoid classloading Fabric8 during build time
        return new DefaultK8sClientProvider();
    }

    @Override
    public void init(Config.Scope config) {
        // Nothing to config
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        // Nothing to manage
    }

    @Override
    public void close() {
        // Nothing to close
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }
}
