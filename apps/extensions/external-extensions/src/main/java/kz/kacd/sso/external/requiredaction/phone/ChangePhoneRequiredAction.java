package kz.kacd.sso.external.requiredaction.phone;

import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import kz.kacd.sso.external.model.page.ExternalMessages;
import kz.kacd.sso.external.model.page.ExternalRegistrationPage;
import org.jboss.logging.Logger;
import org.keycloak.authentication.RequiredActionContext;
import org.keycloak.authentication.RequiredActionProvider;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.models.UserModel;
import org.keycloak.models.utils.FormMessage;

import java.util.Collections;
import java.util.List;

public class ChangePhoneRequiredAction implements RequiredActionProvider {
    public static final String PROVIDER_ID = "change-phone";
    private static final Logger log = Logger.getLogger(ChangePhoneRequiredAction.class);

    @Override
    public void evaluateTriggers(RequiredActionContext context) {
        // No triggers
    }

    @Override
    public void requiredActionChallenge(RequiredActionContext context) {
        log.debugf("Creating update phone number action for user {} ...", context.getUser().getUsername());
        context.challenge(createForm(context, null, Collections.emptyList()));
    }

    @Override
    public void processAction(RequiredActionContext context) {
        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
        String newPhone = formData.getFirst(ExternalRegistrationPage.FIELD_PHONE_NUMBER);
        newPhone = newPhone == null ? null : newPhone.replace(" ", "")
                .replace("(", "")
                .replace(")", "");

        if (newPhone == null || !newPhone.matches("\\+\\d{11}")) {
            log.debugf("Invalid phone received!");
            context.challenge(
                    createForm(
                            context,
                            newPhone,
                            Collections.singletonList(
                                    new FormMessage(
                                            ExternalRegistrationPage.FIELD_PHONE_NUMBER,
                                            ExternalMessages.INVALID_PHONE_NUMBER)
                            )
                    )
            );
            return;
        }

        UserModel user = context.getUser();

        user.setSingleAttribute(ExternalRegistrationPage.FIELD_PHONE_NUMBER, newPhone);
        user.removeRequiredAction(PROVIDER_ID);

        log.infof("Changed phone number for user {}.", user.getUsername());

        context.success();
    }

    private Response createForm(RequiredActionContext context, String value, List<FormMessage> messages) {
        LoginFormsProvider form = context.form();
        if (context.getUser().getFirstAttribute(ExternalRegistrationPage.FIELD_PHONE_NUMBER) != null) {
            form.setAttribute("initialSetting", "false");
            form.setAttribute(
                    ExternalRegistrationPage.FIELD_PHONE_NUMBER,
                    value != null ? value : context.getUser().getFirstAttribute(ExternalRegistrationPage.FIELD_PHONE_NUMBER)
            );
        } else {
            form.setAttribute("initialSetting", "true");
            if (value != null) {
                form.setAttribute(ExternalRegistrationPage.FIELD_PHONE_NUMBER, value);
            }
        }

        if (!messages.isEmpty()) {
            messages.forEach(form::addError);
        }

        return form.createForm("update-phone.ftl");
    }

    @Override
    public void close() {
        // Nothing to close
    }
}
