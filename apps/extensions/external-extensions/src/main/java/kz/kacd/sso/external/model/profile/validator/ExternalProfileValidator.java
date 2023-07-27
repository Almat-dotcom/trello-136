package kz.kacd.sso.external.model.profile.validator;

import kz.kacd.sso.external.model.profile.ExternalAttributes;
import org.jboss.logging.Logger;
import org.keycloak.models.KeycloakSession;
import org.keycloak.userprofile.ValidationException;

public class ExternalProfileValidator {
    private static final Logger log = Logger.getLogger(ExternalProfileValidator.class);

    private final KeycloakSession session;

    public ExternalProfileValidator(KeycloakSession session) {
        this.session = session;
    }

    public ValidationException validate(ExternalAttributes attributes) {
        log.debug("Validating external attributes ...");
        ValidationException result = new ValidationException();

        new SimpleExternalAttributesValidator(result).validate(attributes);
        new FieldConsistencyValidator(result).validate(attributes);
        if (!result.getErrors().isEmpty()) {
            return result;
        }

        new UserDuplicateValidator(result, session).validate(attributes);
        if (!result.getErrors().isEmpty()) {
            return result;
        }

        new LegalClientValidator(result, session).validate(attributes);
        if (!result.getErrors().isEmpty()) {
            return result;
        }

        return null;
    }
}
