package kz.kacd.sso.external.resource.logininfo;

import kz.kacd.sso.external.resource.BaseRealmResourceProvider;
import kz.kacd.sso.external.resource.otp.OTPResource;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;

public class LastLoginResourceProvider extends BaseRealmResourceProvider {

    protected LastLoginResourceProvider(KeycloakSession session) {
        super(session);
    }

    @Override
    protected Object getRealmResource() {
        RealmModel realm = session.getContext().getRealm();
        LastLoginResource lastLoginResource = new LastLoginResource(realm,session);
        ResteasyProviderFactory.getInstance().injectProperties(lastLoginResource);
        lastLoginResource.setup();
        return lastLoginResource;
    }
}