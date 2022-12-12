package kz.kacd.sso.realm.config;

import com.google.auto.service.AutoService;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

@AutoService(KeycloakRealmConfigurerFactory.class)
public class DefaultRealmConfigurerFactory implements KeycloakRealmConfigurerFactory {
    private static final String PROVIDER_ID = "default-realm-configurer";

    @Override
    public KeycloakRealmConfigurer create(KeycloakSession session) {
        return new DefaultRealmConfigurer(session);
    }

    @Override
    public void init(Config.Scope config) {
        // Nothing to config
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        // Nothing to do after init
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
