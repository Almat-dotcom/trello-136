package kz.kacd.sso.external.requiredaction.phone;

import com.google.auto.service.AutoService;
import org.keycloak.Config;
import org.keycloak.authentication.RequiredActionFactory;
import org.keycloak.authentication.RequiredActionProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

@AutoService(RequiredActionFactory.class)
public class ChangePhoneRequiredActionFactory implements RequiredActionFactory {

    @Override
    public String getDisplayText() {
        return "User needs to change their phone number.";
    }

    @Override
    public RequiredActionProvider create(KeycloakSession session) {
        return new ChangePhoneRequiredAction();
    }

    @Override
    public void init(Config.Scope config) {
        // Nothing to init
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        // Nothing to do
    }

    @Override
    public void close() {
        // Nothing to close
    }

    @Override
    public String getId() {
        return ChangePhoneRequiredAction.PROVIDER_ID;
    }
}
