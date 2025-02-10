package kz.kacd.sso.external.resource.otp;

import kz.kacd.sso.external.resource.BaseRealmResourceProvider;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;

public class OTPResourceProvider extends BaseRealmResourceProvider {

    protected OTPResourceProvider(KeycloakSession session) {
        super(session);
    }

    @Override
    protected Object getRealmResource() {
        RealmModel realm = session.getContext().getRealm();
        OTPResource otpResource = new OTPResource(session, realm);
        ResteasyProviderFactory.getInstance().injectProperties(otpResource);
        otpResource.setup();
        return otpResource;
    }
}