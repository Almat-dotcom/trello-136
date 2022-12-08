package kz.kacd.sso.k8s.secret;

import com.google.auto.service.AutoService;
import kz.kacd.sso.k8s.K8sClientProvider;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

@AutoService(SecretValueProviderFactory.class)
public class K8sSecretValueProviderFactory implements SecretValueProviderFactory {
    private static final String PROVIDER_ID = "k8s-secret-value-provider";

    @Override
    public SecretValueProvider create(KeycloakSession session) {
        K8sClientProvider clientProvider = session.getProvider(K8sClientProvider.class);
        return new K8sSecretValueProvider(clientProvider.getClient());
    }

    @Override
    public void init(Config.Scope config) {
        // Nothing to configure
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        // Nothing to post init
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
