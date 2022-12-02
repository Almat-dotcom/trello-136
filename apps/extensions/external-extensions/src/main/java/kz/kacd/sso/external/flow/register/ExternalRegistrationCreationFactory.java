package kz.kacd.sso.external.flow.register;

import com.google.auto.service.AutoService;
import org.keycloak.Config;
import org.keycloak.authentication.FormAction;
import org.keycloak.authentication.FormActionFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.Collections;
import java.util.List;

@AutoService(FormActionFactory.class)
public class ExternalRegistrationCreationFactory implements FormActionFactory {
    public static final String PROVIDER_ID = "external-user-creation";

    private static final AuthenticationExecutionModel.Requirement[] REQUIREMENT_CHOICES =
            new AuthenticationExecutionModel.Requirement[] {
                    AuthenticationExecutionModel.Requirement.REQUIRED,
                    AuthenticationExecutionModel.Requirement.DISABLED
            };

    @Override
    public String getDisplayType() {
        return "Create External User Profile";
    }

    @Override
    public String getReferenceCategory() {
        return null;
    }

    @Override
    public boolean isConfigurable() {
        return false;
    }

    @Override
    public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
        return REQUIREMENT_CHOICES;
    }

    @Override
    public boolean isUserSetupAllowed() {
        return false;
    }

    @Override
    public String getHelpText() {
        return "Creates user profile on registration";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return Collections.emptyList();
    }

    @Override
    public FormAction create(KeycloakSession session) {
        return new ExternalRegistrationCreation();
    }

    @Override
    public void init(Config.Scope config) {
        // No config
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        // No post init actions
    }

    @Override
    public void close() {
        // Nothing to close
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }
}
