package kz.kacd.sso.external.resource.organization;

import kz.kacd.sso.external.resource.BaseRealmResourceProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;

public class OrganizationsResourceProvider extends BaseRealmResourceProvider {

    protected OrganizationsResourceProvider(KeycloakSession session) {
        super(session);
    }

    @Override
    protected Object getRealmResource() {
        RealmModel realm = session.getContext().getRealm();
        OrganizationsResource orgsRes = new OrganizationsResource(session, realm);
        orgsRes.setup();
        return orgsRes;
    }
}
