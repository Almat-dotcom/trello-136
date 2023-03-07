package kz.kacd.sso.k8s.realm;

import com.google.auto.service.AutoService;
import kz.kacd.sso.k8s.K8sClientProvider;
import kz.kacd.sso.k8s.realm.repository.K8sRealmRepository;
import org.jboss.logging.Logger;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

@AutoService(K8sRealmProviderFactory.class)
public class K8sRealmProviderFactoryImpl implements K8sRealmProviderFactory {
    private static final Logger log = Logger.getLogger(K8sRealmProviderFactoryImpl.class);

    private static final String PROVIDER_ID = "default-k8s-realm-provider";

    @Override
    public K8sRealmProvider create(KeycloakSession session) {
        log.debug("Creating new k8s realm provider ...");
        K8sClientProvider clientProvider = session.getProvider(K8sClientProvider.class);
        K8sRealmRepository repository = new K8sRealmRepository(clientProvider.getClient());
        return new K8sRealmProviderImpl(session, repository);
    }

    @Override
    public void init(Config.Scope config) {
        // Nothing to configure
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        // Nothing to listen
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
