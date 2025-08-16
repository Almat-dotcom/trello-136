package kz.kacd.sso.k8s.client;

import com.google.auto.service.AutoService;
import kz.kacd.sso.k8s.K8sClientProvider;
import kz.kacd.sso.k8s.client.repository.IsolatedK8sClientRepository;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

@AutoService(K8sClientSpecProviderFactory.class)
public class IsolatedK8sClientSpecProviderFactoryImpl implements K8sClientSpecProviderFactory {

    private static final String PROVIDER_ID = "isolated-k8s-client-provider";

    @Override
    public K8sClientSpecProvider create(KeycloakSession session) {
        K8sClientProvider clientProvider = session.getProvider(K8sClientProvider.class);
        return new IsolatedK8sClientSpecProviderImpl(session, new IsolatedK8sClientRepository(clientProvider.getClient()));
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

