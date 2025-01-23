package kz.kacd.sso.external.flow.login;

import com.google.auto.service.AutoService;
import org.keycloak.Config;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.Collections;
import java.util.List;

@AutoService(AuthenticatorFactory.class)
public class ConditionalOTPAuthenticatorFactory implements AuthenticatorFactory {

    public static final String PROVIDER_ID = "conditional-otp-authenticator";  // NEW ID

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public Authenticator create(KeycloakSession session) {
        return new ConditionalOTPAuthenticator();
    }

    @Override
    public void init(Config.Scope config) {
        // No init needed
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        // No post init needed
    }

    @Override
    public void close() {
        // No close needed
    }

    @Override
    public String getDisplayType() {
        return "Conditional OTP Authenticator";
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
        return "Checks if OTP is enabled for the user and challenges with OTP form if so.";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return Collections.emptyList();
    }

    private static final AuthenticationExecutionModel.Requirement[] REQUIREMENT_CHOICES = new AuthenticationExecutionModel.Requirement[]{
            AuthenticationExecutionModel.Requirement.REQUIRED, AuthenticationExecutionModel.Requirement.ALTERNATIVE, AuthenticationExecutionModel.Requirement.DISABLED
    };
}