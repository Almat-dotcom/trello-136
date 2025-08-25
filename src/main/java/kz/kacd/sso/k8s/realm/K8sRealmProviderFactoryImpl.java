package kz.kacd.sso.k8s.realm;

import com.google.auto.service.AutoService;
import kz.kacd.sso.k8s.realm.repository.K8sRealmRepository;
import org.jboss.logging.Logger;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

@AutoService(K8sRealmProviderFactory.class)
public class K8sRealmProviderFactoryImpl implements K8sRealmProviderFactory {

    private static final Logger log = Logger.getLogger(K8sRealmProviderFactoryImpl.class);
    private static final String PROVIDER_ID = "k8s-realm-provider";

    @Override
    public K8sRealmProvider create(KeycloakSession session) {
        log.debug("Creating new k8s realm provider ...");
        try {
            @SuppressWarnings("unchecked")
            Class<org.keycloak.provider.Provider> k8sClientProviderClass = (Class<org.keycloak.provider.Provider>) Class.forName("kz.kacd.sso.provider.K8sClientProvider");
            Object clientProvider = session.getProvider(k8sClientProviderClass);
            Object client = clientProvider.getClass().getMethod("getClient").invoke(clientProvider);
            K8sRealmRepository repository = new K8sRealmRepository((io.fabric8.kubernetes.client.KubernetesClient) client);
            return new K8sRealmProviderImpl(session, repository);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create K8sRealmProvider", e);
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

