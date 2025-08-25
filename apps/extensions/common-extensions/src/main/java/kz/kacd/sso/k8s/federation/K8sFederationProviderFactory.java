package kz.kacd.sso.k8s.federation;

import com.google.auto.service.AutoService;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderFactory;

@AutoService(ProviderFactory.class)
public class K8sFederationProviderFactory implements ProviderFactory<K8sFederationProvider> {
    @Override
    public K8sFederationProvider create(KeycloakSession session) {
        return new DefaultK8sFederationProvider();
    }

    @Override
    public void init(Config.Scope config) {}

    @Override
    public void postInit(KeycloakSessionFactory factory) {}

    @Override
    public void close() {}

    @Override
    public String getId() {
        return "k8s-federation";
    }
}

