package kz.kacd.sso.external.flow.login;

import com.google.auto.service.AutoService;
import kz.kacd.sso.external.flow.login.eds.EdsAuthenticator;
import kz.kacd.sso.external.flow.login.ldap.NoLdapUserValidator;
import kz.kacd.sso.external.flow.login.legal.LegalUserValidator;
import kz.kacd.sso.external.sign.SignatureValidator;
import org.jboss.logging.Logger;
import org.keycloak.Config;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@AutoService(AuthenticatorFactory.class)
public class EdsOrUsernamePasswordFormFactory implements AuthenticatorFactory {
    private static final Logger log = Logger.getLogger(EdsOrUsernamePasswordFormFactory.class);
    private static final String PROVIDER_ID = "eds-username-password-form";
    private static final AuthenticationExecutionModel.Requirement[] REQUIREMENT_CHOICES =
            new AuthenticationExecutionModel.Requirement[]{
                    AuthenticationExecutionModel.Requirement.REQUIRED
            };

    @Override
    public String getDisplayType() {
        return "EDS or Username Password Form";
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
        return "This authenticator provides two options to authenticate: EDS or username and password";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return Collections.emptyList();
    }

    @Override
    public Authenticator create(KeycloakSession session) {
        log.info("Vooot tut");
        return new ExtendedUsernamePasswordForm(
                Collections.singletonList(new EdsAuthenticator(new SignatureValidator())),
                Arrays.asList(new LegalUserValidator(), new NoLdapUserValidator())
        );
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
