package kz.kacd.sso.resource.profile;

import kz.kacd.sso.resource.BaseRealmResourceProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;

public class RealmProfileResourceProvider extends BaseRealmResourceProvider {

    protected RealmProfileResourceProvider(KeycloakSession session) {
        super(session);
    }

    @Override
    protected Object getRealmResource() {
        RealmModel realm = session.getContext().getRealm();
        RealmProfileResource resource = new RealmProfileResource(realm);
        resource.setup();
        return resource;
    }
}
