package kz.kacd.sso.k8s.federation;

import com.google.auto.service.AutoService;
import kz.kacd.sso.k8s.K8sClientProvider;
import kz.kacd.sso.k8s.federation.repository.K8sFederationRepository;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

@AutoService(K8sFederationProviderFactory.class)
public class K8sFederationProviderFactoryImpl implements K8sFederationProviderFactory {

    private static final String PROVIDER_ID = "default-k8s-federation-provider";

    @Override
    public K8sFederationProvider create(KeycloakSession session) {
        K8sClientProvider clientProvider = session.getProvider(K8sClientProvider.class);
        return new K8sFederationProviderImpl(session, new K8sFederationRepository(clientProvider.getClient()));
    }

    @Override
    public void init(Config.Scope config) {
        // Nothing to init
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        // Nothing to do
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
