package kz.kacd.sso.external.flow.login.ldap;

import kz.kacd.sso.external.flow.login.AuthenticatorValidator;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.events.Errors;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.UserModel;
import org.keycloak.services.managers.AuthenticationManager;

public class NoLdapUserValidator implements AuthenticatorValidator {
    @Override
    public Error validate(UserModel user, KeycloakSession session, AuthenticationFlowContext context) {
        if (user.getAttributes().containsKey("division")) {
            return new Error(Errors.INVALID_USER_CREDENTIALS, AuthenticationManager.FORM_USERNAME);
        }
        return null;
    }
}
