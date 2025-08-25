package kz.kacd.sso.realm.config;

import com.google.auto.service.AutoService;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderFactory;

@AutoService(ProviderFactory.class)
public class KeycloakRealmConfigurerFactory implements ProviderFactory<KeycloakRealmConfigurer> {
    @Override
    public KeycloakRealmConfigurer create(KeycloakSession session) {
        return new DefaultKeycloakRealmConfigurer();
    }

    @Override
    public void init(Config.Scope config) {}

    @Override
    public void postInit(KeycloakSessionFactory factory) {}

    @Override
    public void close() {}

    @Override
    public String getId() {
        return "keycloak-realm-configurer";
    }
}




