package kz.kacd.sso.external.flow;

import kz.kacd.sso.external.model.profile.ExternalUserProfile;
import kz.kacd.sso.external.model.profile.ExternalUserProfileProvider;
import org.jboss.logging.Logger;
import org.keycloak.authentication.FormAction;
import org.keycloak.authentication.FormContext;
import org.keycloak.authentication.ValidationContext;
import org.keycloak.events.Details;
import org.keycloak.events.Errors;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.utils.FormMessage;
import org.keycloak.services.messages.Messages;
import org.keycloak.services.validation.Validation;
import org.keycloak.userprofile.ValidationException;

import javax.ws.rs.core.MultivaluedMap;
import java.util.List;

public class ExternalRegistrationProfile implements FormAction {
    private static final Logger log = Logger.getLogger(ExternalRegistrationProfile.class);

    @Override
    public void buildPage(FormContext formContext, LoginFormsProvider form) {
        // Nothing to add
    }

    @Override
    public void validate(ValidationContext context) {
        log.debug("Validating form ...");
        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();

        context.getEvent().detail(Details.REGISTER_METHOD, "form");

        ExternalUserProfileProvider provider = new ExternalUserProfileProvider(context.getSession());
        ExternalUserProfile profile = provider.create(formData);

        ValidationException pve = profile.validate();

        if (pve == null) {
            context.success();
            return;
        }

        List<FormMessage> errors = Validation.getFormErrorsFromValidation(pve.getErrors());

        if (pve.hasError(Messages.EMAIL_EXISTS, Messages.INVALID_EMAIL)) {
            context.getEvent().detail(Details.EMAIL, profile.email());
        }

        if (pve.hasError(Messages.EMAIL_EXISTS)) {
            context.error(Errors.EMAIL_IN_USE);
        } else
            context.error(Errors.INVALID_REGISTRATION);

        context.validationError(formData, errors);
    }

    @Override
    public void success(FormContext context) {
        UserModel model = context.getUser();
        ExternalUserProfileProvider provider = new ExternalUserProfileProvider(context.getSession());
        provider.create(context.getHttpRequest().getDecodedFormParameters(), model).update();
    }

    @Override
    public boolean requiresUser() {
        return false;
    }

    @Override
    public boolean configuredFor(KeycloakSession keycloakSession, RealmModel realmModel, UserModel userModel) {
        return true;
    }

    @Override
    public void setRequiredActions(KeycloakSession keycloakSession, RealmModel realmModel, UserModel userModel) {
        // There are no required actions to be configured
    }

    @Override
    public void close() {
        // There is nothing to close
    }
}
