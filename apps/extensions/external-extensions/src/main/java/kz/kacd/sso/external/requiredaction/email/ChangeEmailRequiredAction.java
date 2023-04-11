package kz.kacd.sso.external.requiredaction.email;

import org.jboss.logging.Logger;
import org.keycloak.authentication.InitiatedActionSupport;
import org.keycloak.authentication.RequiredActionContext;
import org.keycloak.authentication.RequiredActionProvider;
import org.keycloak.authentication.forms.RegistrationPage;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.models.UserModel;
import org.keycloak.models.utils.FormMessage;
import org.keycloak.services.messages.Messages;

import javax.ws.rs.core.MultivaluedMap;

public class ChangeEmailRequiredAction implements RequiredActionProvider {
    private static final Logger log = Logger.getLogger(ChangeEmailRequiredAction.class);

    private static final String FORM = "update-email.ftl";

    public static final String PROVIDER_ID = "change-email";

    @Override
    public InitiatedActionSupport initiatedActionSupport() {
        return InitiatedActionSupport.SUPPORTED;
    }

    @Override
    public void evaluateTriggers(RequiredActionContext context) {
        if (context.getUser().getEmail().endsWith("example.com")) {
            context.getUser().addRequiredAction(PROVIDER_ID);
        }
    }

    @Override
    public void requiredActionChallenge(RequiredActionContext context) {
        log.debugf("Creating update email form for user {} ...", context.getUser().getUsername());

        LoginFormsProvider form = context.form();

        context.challenge(form.createForm(FORM));
    }

    @Override
    public void processAction(RequiredActionContext context) {
        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
        String newEmail = formData.getFirst(RegistrationPage.FIELD_EMAIL);

        if (newEmail == null || !newEmail.contains("@") || newEmail.endsWith("example.com")) {
            log.debugf("Invalid email received.");
            LoginFormsProvider form = context.form();
            form.setAttribute("email", newEmail);
            form.addError(new FormMessage(RegistrationPage.FIELD_EMAIL, Messages.INVALID_EMAIL));
            context.challenge(form.createForm(FORM));
            return;
        }

        UserModel user = context.getUser();
        user.setEmail(newEmail);
        user.addRequiredAction(UserModel.RequiredAction.VERIFY_EMAIL);
        user.removeRequiredAction(PROVIDER_ID);

        log.infof("User {} successfully updated they email.", context.getUser().getUsername());

        context.success();
    }

    @Override
    public void close() {
        // Nothing to close
    }
}
