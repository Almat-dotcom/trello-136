package kz.kacd.sso.k8s.federation;

import com.google.auto.service.AutoService;
import kz.kacd.sso.k8s.federation.repository.K8sFederationRepository;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

@AutoService(K8sFederationProviderFactory.class)
public class K8sFederationProviderFactoryImpl implements K8sFederationProviderFactory {

    private static final String PROVIDER_ID = "k8s-federation-provider";

    @Override
    public K8sFederationProvider create(KeycloakSession session) {
        try {
            @SuppressWarnings("unchecked")
            Class<org.keycloak.provider.Provider> k8sClientProviderClass = (Class<org.keycloak.provider.Provider>) Class.forName("kz.kacd.sso.provider.K8sClientProvider");
            Object clientProvider = session.getProvider(k8sClientProviderClass);
            Object client = clientProvider.getClass().getMethod("getClient").invoke(clientProvider);
            return new K8sFederationProviderImpl(session, new K8sFederationRepository((io.fabric8.kubernetes.client.KubernetesClient) client));
        } catch (Exception e) {
            throw new RuntimeException("Failed to create K8sFederationProvider", e);
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

