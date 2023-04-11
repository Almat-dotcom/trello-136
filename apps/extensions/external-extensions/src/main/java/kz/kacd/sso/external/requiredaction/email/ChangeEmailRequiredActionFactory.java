package kz.kacd.sso.external.requiredaction.email;

import com.google.auto.service.AutoService;
import org.keycloak.Config;
import org.keycloak.authentication.RequiredActionFactory;
import org.keycloak.authentication.RequiredActionProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

@AutoService(RequiredActionFactory.class)
public class ChangeEmailRequiredActionFactory implements RequiredActionFactory {

    @Override
    public String getDisplayText() {
        return "Should update his email because it's invalid";
    }

    @Override
    public RequiredActionProvider create(KeycloakSession session) {
        return new ChangeEmailRequiredAction();
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
        return ChangeEmailRequiredAction.PROVIDER_ID;
    }
}
