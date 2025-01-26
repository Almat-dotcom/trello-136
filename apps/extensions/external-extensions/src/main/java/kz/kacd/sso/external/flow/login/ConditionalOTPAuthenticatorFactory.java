package kz.kacd.sso.external.flow.login;

import com.google.auto.service.AutoService;
import org.jboss.logging.Logger;
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

    private static final Logger logger = Logger.getLogger(ConditionalOTPAuthenticatorFactory.class);
    public static final String PROVIDER_ID = "conditional-otp-authenticator";
    private static final ConditionalOTPAuthenticator SINGLETON = new ConditionalOTPAuthenticator();

    @Override
    public Authenticator create(KeycloakSession session) {
        return SINGLETON;
    }

    @Override
    public void init(Config.Scope config) {
        logger.info("Initializing ConditionalOTPAuthenticatorFactory");
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        // No additional initialization required
    }

    @Override
    public void close() {
        // No additional resources to clean up
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public String getDisplayType() {
        return "Conditional OTP Authenticator";
    }

    @Override
    public String getReferenceCategory() {
        return "otp";
    }

    @Override
    public boolean isConfigurable() {
        return false;
    }

    @Override
    public boolean isUserSetupAllowed() {
        return true;
    }

    @Override
    public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
        return new AuthenticationExecutionModel.Requirement[]{
                AuthenticationExecutionModel.Requirement.REQUIRED,
                AuthenticationExecutionModel.Requirement.ALTERNATIVE,
                AuthenticationExecutionModel.Requirement.DISABLED
        };
    }

    @Override
    public String getHelpText() {
        return "Conditionally validates OTP. If OTP is configured, it will be required; otherwise, the step will be skipped.";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return Collections.emptyList();
    }
}
