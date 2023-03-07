package kz.kacd.sso.external.flow.login;

import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.UserModel;

/**
 * Validator of authentication after user has been logged in.
 */
public interface AuthenticatorValidator {

    /**
     * Validates if current user can be logged in.
     */
    Error validate(UserModel user, KeycloakSession session, AuthenticationFlowContext context);

    final class Error {
        private final String message;
        private final String field;

        public Error(String message, String field) {
            this.message = message;
            this.field = field;
        }

        public String getMessage() {
            return message;
        }

        public String getField() {
            return field;
        }
    }
}
