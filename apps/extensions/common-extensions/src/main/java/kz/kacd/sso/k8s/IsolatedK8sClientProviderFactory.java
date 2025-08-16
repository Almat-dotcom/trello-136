package kz.kacd.sso.k8s;

import com.google.auto.service.AutoService;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

@AutoService(K8sClientProviderFactory.class)
public class IsolatedK8sClientProviderFactory implements K8sClientProviderFactory {
    private static final String PROVIDER_ID = "isolated-k8s-client-provider";

    private static final IsolatedK8sClientProvider INSTANCE = new IsolatedK8sClientProvider();

    @Override
    public K8sClientProvider create(KeycloakSession session) {
        return INSTANCE;
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
        INSTANCE.close();
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }
}

