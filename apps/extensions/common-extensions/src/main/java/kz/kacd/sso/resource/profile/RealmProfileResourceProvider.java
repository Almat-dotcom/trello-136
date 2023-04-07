package kz.kacd.sso.resource.profile;

import kz.kacd.sso.resource.BaseRealmResourceProvider;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
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
        ResteasyProviderFactory.getInstance().injectProperties(resource);
        resource.setup();
        return resource;
    }
}
