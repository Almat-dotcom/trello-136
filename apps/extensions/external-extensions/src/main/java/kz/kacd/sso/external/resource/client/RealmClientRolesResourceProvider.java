package kz.kacd.sso.external.resource.client;

import kz.kacd.sso.external.resource.BaseRealmResourceProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;

public class RealmClientRolesResourceProvider extends BaseRealmResourceProvider {

    protected RealmClientRolesResourceProvider(KeycloakSession session) {
        super(session);
    }

    @Override
    protected Object getRealmResource() {
        RealmModel realm = session.getContext().getRealm();
        RealmClientRolesResource resource = new RealmClientRolesResource(session, realm);
        resource.setup();
        return resource;
    }
}
