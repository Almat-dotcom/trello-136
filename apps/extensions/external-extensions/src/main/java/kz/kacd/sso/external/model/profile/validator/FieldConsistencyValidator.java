package kz.kacd.sso.external.model.profile.validator;

import kz.kacd.sso.external.model.page.ExternalMessages;
import kz.kacd.sso.external.model.page.ExternalRegistrationPage;
import kz.kacd.sso.external.model.profile.ExternalAttributes;
import org.keycloak.validate.ValidationError;

import java.util.function.Consumer;

public class FieldConsistencyValidator {
    private static final String VALIDATOR_ID = "field-consistency-external-attribute-validator";

    private final Consumer<ValidationError> listener;

    public FieldConsistencyValidator(Consumer<ValidationError> listener) {
        this.listener = listener;
    }

    public void validate(ExternalAttributes attributes) {
        if (attributes.clientType().equals(ExternalRegistrationPage.CLIENT_LEGAL)) {
            validateLegalClient(attributes);
        }
        if (attributes.residency().equals(ExternalRegistrationPage.RESIDENT)) {
            validateResident(attributes);
        }
    }

    private void validateLegalClient(ExternalAttributes attributes) {
        if (attributes.residency().equals(ExternalRegistrationPage.RESIDENT)) {
            validateLegalResident(attributes);
        }
        if (attributes.residency().equals(ExternalRegistrationPage.NON_RESIDENT)) {
            validateLegalNonResident(attributes);
        }
        if (attributes.legalRole() == null || attributes.legalRole().isEmpty()) {
            listener.accept(error(ExternalRegistrationPage.FIELD_LEGAL_ROLE, ExternalMessages.MISSING_LEGAL_ROLE));
        }
    }

    private void validateLegalResident(ExternalAttributes attributes) {
        if (attributes.bin() == null || attributes.bin().isEmpty()) {
            listener.accept(error(ExternalRegistrationPage.FIELD_BIN, ExternalMessages.MISSING_BIN));
        }
        if (attributes.eds() == null || attributes.eds().isEmpty()) {
            listener.accept(error(ExternalRegistrationPage.FIELD_EDS, ExternalMessages.MISSING_EDS));
        }
    }

    private void validateLegalNonResident(ExternalAttributes attributes) {
        if (
                ExternalRegistrationPage.ROLE_EMPLOYEE.equals(attributes.legalRole())
                        && (attributes.bin() == null || attributes.bin().isEmpty())
        ) {
            listener.accept(error(ExternalRegistrationPage.FIELD_BIN, ExternalMessages.INVALID_BIN));
        }
    }

    private void validateResident(ExternalAttributes attributes) {
        if (attributes.iin() == null || attributes.iin().isEmpty()) {
            listener.accept(error(ExternalRegistrationPage.FIELD_IIN, ExternalMessages.MISSING_IIN));
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
