package kz.kacd.sso.external.resource;

import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;

public class OrganizationsResourceProvider extends BaseRealmResourceProvider {

    protected OrganizationsResourceProvider(KeycloakSession session) {
        super(session);
    }

    @Override
    protected Object getRealmResource() {
        RealmModel realm = session.getContext().getRealm();
        OrganizationsResource organization = new OrganizationsResource(realm);
        ResteasyProviderFactory.getInstance().injectProperties(organization);
        organization.setup();
        return organization;
    }
}
