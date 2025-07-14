package kz.kacd.sso.resource.config;

import kz.kacd.sso.resource.BaseRealmResourceProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;

public class ConfigurationResourceProvider extends BaseRealmResourceProvider {

    protected ConfigurationResourceProvider(KeycloakSession session) {
        super(session);
    }

    @Override
    protected Object getRealmResource() {
        RealmModel realm = session.getContext().getRealm();
        ConfigurationResource res = new ConfigurationResource(session, realm);
        res.setup();
        return res;
    }
}
