package kz.kacd.sso.login;

import com.google.auto.service.AutoService;
import kz.kacd.sso.sign.SignatureValidator;
import org.jboss.logging.Logger;
import org.keycloak.Config;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.Collections;
import java.util.List;

@AutoService(AuthenticatorFactory.class)
public class AfrUsernamePasswordFormFactory implements AuthenticatorFactory {
    private static final Logger log = Logger.getLogger(AfrUsernamePasswordFormFactory.class);

    public static final String PROVIDER_ID = "afr-username-password-form";

    private static final AuthenticationExecutionModel.Requirement[] REQUIREMENT_CHOICES =
            new AuthenticationExecutionModel.Requirement[]{
                    AuthenticationExecutionModel.Requirement.REQUIRED
            };

    @Override
    public String getDisplayType() {
        log.info("Afr user factory page");
        return "AFR Username Password form";
    }

    @Override
    public String getReferenceCategory() {
        return PasswordCredentialModel.TYPE;
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
        return "This authenticator provides second factor as EDS to log in.";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return Collections.emptyList();
    }

    @Override
    public Authenticator create(KeycloakSession session) {
        return new AfrUsernamePasswordForm(new SignatureValidator());
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
        return PROVIDER_ID;
    }
}
