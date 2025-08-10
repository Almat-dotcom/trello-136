package kz.kacd.sso.client.config;

import com.google.auto.service.AutoService;
import org.jboss.logging.Logger;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

@AutoService(ClientConfigurerFactory.class)
public class DefaultClientConfigurerFactory implements ClientConfigurerFactory {
    private static final Logger logger = Logger.getLogger(DefaultClientConfigurerFactory.class);
    private static final String PROVIDER_ID = "default-client-configurer";

    @Override
    public ClientConfigurer create(KeycloakSession session) {
        return new DefaultClientConfigurer(session);
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
