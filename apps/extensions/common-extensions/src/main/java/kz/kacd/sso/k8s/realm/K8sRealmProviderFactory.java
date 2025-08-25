package kz.kacd.sso.k8s.realm;

import com.google.auto.service.AutoService;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderFactory;

@AutoService(ProviderFactory.class)
public class K8sRealmProviderFactory implements ProviderFactory<K8sRealmProvider> {
    @Override
    public K8sRealmProvider create(KeycloakSession session) {
        return new DefaultK8sRealmProvider();
    }

    @Override
    public void init(Config.Scope config) {}

    @Override
    public void postInit(KeycloakSessionFactory factory) {}

    @Override
    public void close() {}

    @Override
    public String getId() {
        return "k8s-realm";
    }
}

