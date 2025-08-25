package kz.kacd.sso.federation;

import com.google.auto.service.AutoService;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderFactory;

@AutoService(ProviderFactory.class)
public class FederationConfigurerFactory implements ProviderFactory<FederationConfigurer> {
    @Override
    public FederationConfigurer create(KeycloakSession session) {
        return new DefaultFederationConfigurer();
    }

    @Override
    public void init(Config.Scope config) {}

    @Override
    public void postInit(KeycloakSessionFactory factory) {}

    @Override
    public void close() {}

    @Override
    public String getId() {
        return "federation-configurer";
    }
}




