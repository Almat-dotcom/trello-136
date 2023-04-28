package kz.kacd.sso.login;

import kz.kacd.sso.sign.SignatureValidator;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.authenticators.browser.UsernamePasswordForm;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.services.messages.Messages;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

public class AfrUsernamePasswordForm extends UsernamePasswordForm implements Authenticator {

    private final SignatureValidator validator;

    public AfrUsernamePasswordForm(SignatureValidator validator) {
        this.validator = validator;
    }

    @Override
    public void action(AuthenticationFlowContext context) {
        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
        if (formData.getFirst(AfrPage.EDS) == null || formData.getFirst(AfrPage.EDS).isEmpty()) {
            failAuth(context, ExtensionMessages.MISSING_EDS);
            return;
        }

        String eds = formData.getFirst(AfrPage.EDS);
        log.infof("EDS: %s", eds);
        SignatureValidator.Result result = validator.validate(eds);
        log.infof("Validation: %s", result.getType().name());
        switch (result.getType()) {
            case ERROR:
                failAuth(context, result.getMessage());
                return;
            case PHYSICAL:
            case LEGAL:
                checkUser(context, result);
        }
    }

    private void checkUser(AuthenticationFlowContext context, SignatureValidator.Result signature) {
        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
        String email = formData.getFirst("username");

        log.infof("email: %s", email);

        KeycloakSession session = context.getSession();
        RealmModel realm = session.getContext().getRealm();

        UserModel user = session.users().getUserByEmail(realm, email);
        if (user == null) {
            failAuth(context, Messages.INVALID_USER);
            return;
        }

        log.infof("iin: %s", user.getFirstAttribute(AfrPage.IIN));

        if (
                !signature.getIin().equals(user.getFirstAttribute(AfrPage.IIN))
                        || (signature.getBin() != null && !signature.getBin().equals(user.getFirstAttribute(AfrPage.BIN)))
        ) {
            failAuth(context, Messages.INVALID_USER);
            return;
        }

        super.action(context);
    }

    private void failAuth(AuthenticationFlowContext context, String message) {
        boolean clearUser = !isUserAlreadySetBeforeUsernamePasswordAuth(context);
        if (clearUser) {
            context.clearUser();
        }
        Response challenge = challenge(context, message, AfrPage.EDS);
        context.challenge(challenge);
    }
}
