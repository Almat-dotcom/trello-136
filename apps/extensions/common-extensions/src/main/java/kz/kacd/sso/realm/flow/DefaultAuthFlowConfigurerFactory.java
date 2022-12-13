package kz.kacd.sso.realm.flow;

import com.google.auto.service.AutoService;
import org.jboss.logging.Logger;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.RealmModel;
import org.keycloak.models.utils.PostMigrationEvent;

@AutoService(AuthFlowConfigurerFactory.class)
public class DefaultAuthFlowConfigurerFactory implements AuthFlowConfigurerFactory {
    private static final String PROVIDER_ID = "default-auth-flow-configurer";

    @Override
    public AuthFlowConfigurer create(KeycloakSession session) {
        return new DefaultAuthFlowConfigurer(session);
    }

    @Override
    public void init(Config.Scope config) {
        // nothing to init
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        // Nothing to post init
    }

    @Override
    public void close() {
        // nothing to close
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }
}
