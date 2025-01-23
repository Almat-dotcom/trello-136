package kz.kacd.sso.external.flow.login;

import com.google.auto.service.AutoService;
import org.jboss.logging.Logger;
import org.keycloak.Config;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.authentication.authenticators.browser.OTPFormAuthenticator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.Collections;
import java.util.List;

/**
 * Фабрика, которая выдает нашу кастомную реализацию, расширяющую логику OTPFormAuthenticator.
 */
@AutoService(AuthenticatorFactory.class)
public class ExtendedOTPFormAuthenticatorFactory implements AuthenticatorFactory {
    private static final Logger LOG = Logger.getLogger(ExtendedOTPFormAuthenticatorFactory.class);

    // Укажем свой ID, чтобы не конфликтовать с встроенным "auth-otp-form"
    public static final String PROVIDER_ID = "extended-otp-form";

    private static final AuthenticationExecutionModel.Requirement[] REQUIREMENT_CHOICES = {
            AuthenticationExecutionModel.Requirement.REQUIRED,
            AuthenticationExecutionModel.Requirement.ALTERNATIVE,
            AuthenticationExecutionModel.Requirement.DISABLED
    };

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public String getDisplayType() {
        return "Extended OTP Form (custom)";
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
        return "Extended version of the default OTP form authenticator";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return Collections.emptyList();
    }

    @Override
    public Authenticator create(KeycloakSession session) {
        // Возвращаем нашу реализацию, которая "декорирует" (или наследует) логику OTPFormAuthenticator
        return new ExtendedOTPFormAuthenticator();
    }

    @Override
    public void init(Config.Scope config) {
        // Пусто
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        // Пусто
    }

    @Override
    public void close() {
        // Пусто
    }
}
