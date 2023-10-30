package kz.kacd.sso.external.resource;

import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;

public class EBRResourceProvider extends BaseRealmResourceProvider {

    protected EBRResourceProvider(KeycloakSession session) {
        super(session);
    }

    @Override
    protected Object getRealmResource() {
        RealmModel realm = session.getContext().getRealm();
        EBRResource ebr = new EBRResource(realm);
        ResteasyProviderFactory.getInstance().injectProperties(ebr);
        ebr.setup();
        return ebr;
    }
}
