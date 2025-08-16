package kz.kacd.sso.resource.config;

import kz.kacd.sso.resource.BaseRealmResourceProvider;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;

public class ConfigurationResourceProvider extends BaseRealmResourceProvider {

    protected ConfigurationResourceProvider(KeycloakSession session) {
        super(session);
    }

    @Override
    protected Object getRealmResource() {
        RealmModel realm = session.getContext().getRealm();
        ConfigurationResource resource = new ConfigurationResource(session, realm);
        ResteasyProviderFactory.getInstance().injectProperties(resource);
        resource.setup();
        return resource;
    }
}

