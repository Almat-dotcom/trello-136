package kz.kacd.sso.external.resource.id;

import kz.kacd.sso.external.resource.BaseRealmResourceProvider;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;

public class IdResourceProvider extends BaseRealmResourceProvider {
    protected IdResourceProvider(KeycloakSession session) {
        super(session);
    }

    @Override
    protected Object getRealmResource() {
        RealmModel realm = session.getContext().getRealm();
        IdResource id = new IdResource(realm);
        ResteasyProviderFactory.getInstance().injectProperties(id);
        id.setup();
        return id;
    }
}
