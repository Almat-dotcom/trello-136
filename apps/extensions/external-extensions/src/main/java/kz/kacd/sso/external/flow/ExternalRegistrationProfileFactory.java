package kz.kacd.sso.external.flow;

import com.google.auto.service.AutoService;
import java.util.Collections;
import org.keycloak.Config;
import org.keycloak.authentication.FormAction;
import org.keycloak.authentication.FormActionFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.List;

@AutoService(FormActionFactory.class)
public class ExternalRegistrationProfileFactory implements FormActionFactory {
    public static final String PROVIDER_ID = "external-registration-profile-action";

    private static final AuthenticationExecutionModel.Requirement[] REQUIREMENT_CHOICES =
            new AuthenticationExecutionModel.Requirement[] {
                    AuthenticationExecutionModel.Requirement.REQUIRED,
                    AuthenticationExecutionModel.Requirement.DISABLED
            };

    @Override
    public String getDisplayType() {
        return "External profile validation";
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
        return "Validates external profile after registration form submit";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return Collections.emptyList();
    }

    @Override
    public FormAction create(KeycloakSession session) {
        return new ExternalRegistrationProfile();
    }

    @Override
    public void init(Config.Scope config) {
        // There is no config
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        // There is nothing to do after init
    }

    @Override
    public void close() {
        // There is nothing to close
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }
}
