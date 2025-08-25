package kz.kacd.sso.k8s.secret;

import com.google.auto.service.AutoService;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderFactory;

@AutoService(SecretValueProviderFactory.class)
public class K8sSecretValueProviderFactory implements ProviderFactory<SecretValueProvider> {

    private static final String PROVIDER_ID = "k8s-secret-value-provider";

    @Override
    public SecretValueProvider create(KeycloakSession session) {
        try {
            @SuppressWarnings("unchecked")
            Class<org.keycloak.provider.Provider> k8sClientProviderClass = (Class<org.keycloak.provider.Provider>) Class.forName("kz.kacd.sso.provider.K8sClientProvider");
            Object clientProvider = session.getProvider(k8sClientProviderClass);
            Object client = clientProvider.getClass().getMethod("getClient").invoke(clientProvider);
            return new K8sSecretValueProvider((io.fabric8.kubernetes.client.KubernetesClient) client);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create SecretValueProvider", e);
        }
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

