package kz.kacd.sso.external.flow.login;

import com.google.auto.service.AutoService;
import org.jboss.logging.Logger;
import org.keycloak.Config;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.authentication.authenticators.browser.OTPFormAuthenticator;
import org.keycloak.authentication.authenticators.browser.OTPFormAuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.Collections;
import java.util.List;

@AutoService(AuthenticatorFactory.class)
public class ExtendedOTPFormAuthenticatorFactory extends OTPFormAuthenticatorFactory {
    private static final Logger log = Logger.getLogger(ExtendedOTPFormAuthenticatorFactory.class);

    private static final String PROVIDER_ID = "auth-otp-form";
    private static final AuthenticationExecutionModel.Requirement[] REQUIREMENT_CHOICES =
            new AuthenticationExecutionModel.Requirement[]{
                    AuthenticationExecutionModel.Requirement.REQUIRED,
                    AuthenticationExecutionModel.Requirement.ALTERNATIVE,
                    AuthenticationExecutionModel.Requirement.DISABLED
            };

    @Override
    public String getDisplayType() {
        log.info("Extended OTP Form");
        return "Extended OTP Form";
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
        return "Extended OTP Form";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return Collections.emptyList();
    }

    @Override
    public Authenticator create(KeycloakSession session) {
        log.info("Created  OTPFormAuthenticator");
        return session.getProvider(Authenticator.class, OTPFormAuthenticatorFactory.PROVIDER_ID);
    }

    @Override
    public void init(Config.Scope config) {
        // No
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        // No
    }

    @Override
    public void close() {
        // No
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }
}