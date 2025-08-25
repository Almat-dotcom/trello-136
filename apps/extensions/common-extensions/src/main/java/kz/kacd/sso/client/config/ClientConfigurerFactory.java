package kz.kacd.sso.client.config;

import com.google.auto.service.AutoService;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderFactory;

@AutoService(ProviderFactory.class)
public class ClientConfigurerFactory implements ProviderFactory<ClientConfigurer> {
    @Override
    public ClientConfigurer create(KeycloakSession session) {
        return new DefaultClientConfigurer();
    }

    @Override
    public void init(Config.Scope config) {}

    @Override
    public void postInit(KeycloakSessionFactory factory) {}

    @Override
    public void close() {}

    @Override
    public String getId() {
        return "client-configurer";
    }
}




