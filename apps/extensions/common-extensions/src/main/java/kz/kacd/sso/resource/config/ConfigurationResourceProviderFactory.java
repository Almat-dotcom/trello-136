package kz.kacd.sso.resource.config;

import com.google.auto.service.AutoService;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.services.resource.RealmResourceProvider;
import org.keycloak.services.resource.RealmResourceProviderFactory;

@AutoService(RealmResourceProviderFactory.class)
public class ConfigurationResourceProviderFactory implements RealmResourceProviderFactory {
    public static final String PROVIDER_ID = "k8s-config";

    @Override
    public RealmResourceProvider create(KeycloakSession session) {
        return new ConfigurationResourceProvider(session);
    }

    @Override
    public void init(Config.Scope config) {
        // Nothing to init
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

