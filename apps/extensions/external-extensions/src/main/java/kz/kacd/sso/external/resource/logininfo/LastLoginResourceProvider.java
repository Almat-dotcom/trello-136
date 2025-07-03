package kz.kacd.sso.external.resource.logininfo;

import kz.kacd.sso.external.resource.BaseRealmResourceProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;

public class LastLoginResourceProvider extends BaseRealmResourceProvider {

    protected LastLoginResourceProvider(KeycloakSession session) {
        super(session);
    }

    @Override
    protected Object getRealmResource() {
        RealmModel realm = session.getContext().getRealm();
        LastLoginResource resource = new LastLoginResource(session, realm);
        resource.setup();
        return resource;
    }
}