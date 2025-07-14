package kz.kacd.sso.external.resource.ebr;

import kz.kacd.sso.external.resource.BaseRealmResourceProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;

public class EBRResourceProvider extends BaseRealmResourceProvider {

    protected EBRResourceProvider(KeycloakSession session) {
        super(session);
    }

    @Override
    protected Object getRealmResource() {
        RealmModel realm = session.getContext().getRealm();
        EBRResource ebr = new EBRResource(session, realm);
        ebr.setup();
        return ebr;
    }
}