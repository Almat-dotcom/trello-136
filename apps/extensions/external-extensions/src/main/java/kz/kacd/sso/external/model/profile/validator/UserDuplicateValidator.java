package kz.kacd.sso.external.model.profile.validator;

import kz.kacd.sso.external.model.page.ExternalRegistrationPage;
import kz.kacd.sso.external.model.profile.ExternalAttributes;
import kz.kacd.sso.external.model.page.ExternalMessages;
import org.keycloak.authentication.forms.RegistrationPage;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.UserProvider;
import org.keycloak.services.messages.Messages;
import org.keycloak.validate.ValidationError;

import java.util.function.Consumer;

public class UserDuplicateValidator {
    private static final String VALIDATOR_ID = "external-user-duplicate-validator";

    private final Consumer<ValidationError> listener;
    private final KeycloakSession session;

    public UserDuplicateValidator(Consumer<ValidationError> listener, KeycloakSession session) {
        this.listener = listener;
        this.session = session;
    }

    public void validate(ExternalAttributes attributes) {
        RealmModel realm = session.getContext().getRealm();
        UserProvider users = session.users();

        UserModel found = users.getUserByUsername(realm, attributes.username());
        if (found != null) {
            listener.accept(error(ExternalRegistrationPage.FIELD_IIN, ExternalMessages.DUPLICATE_IIN));
        }

        found = users.getUserByEmail(realm, attributes.email());
        if (found != null) {
            listener.accept(error(RegistrationPage.FIELD_EMAIL, Messages.EMAIL_EXISTS));
        }
    }

    private ValidationError error(String field, String message, Object... args) {
        return new ValidationError(
                VALIDATOR_ID,
                field,
                message,
                args
        );
    }
}
