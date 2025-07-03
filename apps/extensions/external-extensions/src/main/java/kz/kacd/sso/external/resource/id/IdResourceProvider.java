package kz.kacd.sso.external.resource.id;

import kz.kacd.sso.external.resource.BaseRealmResourceProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;

public class IdResourceProvider extends BaseRealmResourceProvider {
    protected IdResourceProvider(KeycloakSession session) {
        super(session);
    }

    @Override
    protected Object getRealmResource() {
        RealmModel realm = session.getContext().getRealm();
        IdResource resource = new IdResource(session, realm);
        resource.setup();
        return resource;
    }
}
