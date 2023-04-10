package kz.kacd.sso.resource.client;

import kz.kacd.sso.resource.BaseRealmResourceProvider;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;

public class RealmClientRolesResourceProvider extends BaseRealmResourceProvider {

    protected RealmClientRolesResourceProvider(KeycloakSession session) {
        super(session);
    }

    @Override
    protected Object getRealmResource() {
        RealmModel realm = session.getContext().getRealm();
        RealmClientRolesResource resource = new RealmClientRolesResource(realm);
        ResteasyProviderFactory.getInstance().injectProperties(resource);
        resource.setup();
        return resource;
    }
}
