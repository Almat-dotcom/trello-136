package kz.kacd.sso.external.model.profile.validator;

import kz.kacd.sso.external.model.page.ExternalRegistrationPage;
import kz.kacd.sso.external.model.profile.ExternalAttributes;
import kz.kacd.sso.external.model.page.ExternalMessages;
import org.keycloak.authentication.forms.RegistrationPage;
import org.keycloak.services.messages.Messages;
import org.keycloak.validate.ValidationError;

import java.util.function.Consumer;

public class SimpleExternalAttributesValidator {
    private static final String VALIDATOR_ID = "simple-external-attribute-validator";

    private final Consumer<ValidationError> listener;

    public SimpleExternalAttributesValidator(Consumer<ValidationError> listener) {
        this.listener = listener;
    }

    public void validate(ExternalAttributes attributes) {
        validateEmail(attributes.email(), listener);
        validateIin(attributes.iin(), listener);
        validateFirstName(attributes.firstName(), listener);
        validateLastName(attributes.lastName(), listener);
        validateMiddleName(attributes.middleName(), listener);
        validateClientType(attributes.clientType(), listener);
        validateBin(attributes.bin(), listener);
        validateLegalRole(attributes.legalRole(), listener);
        validateResidency(attributes.residency(), listener);
//        validatePhoneNumber(attributes.phoneNumber(), listener);
    }

    private void validateEmail(String email, Consumer<ValidationError> listener) {
        if (email == null || email.isEmpty()) {
            listener.accept(error(RegistrationPage.FIELD_EMAIL, Messages.MISSING_EMAIL));
            return;
        }
        if (email.length() > 255 || !email.contains(".") || !email.contains("@")) {
            listener.accept(error(RegistrationPage.FIELD_EMAIL, Messages.INVALID_EMAIL));
        }
    }

    private void validateIin(String iin, Consumer<ValidationError> listener) {
        if (iin == null || iin.isEmpty()) {
            return;
        }
        if (iin.length() != 12 || !iin.matches("\\d+")) {
            listener.accept(error(ExternalRegistrationPage.FIELD_IIN, ExternalMessages.INVALID_IIN));
        }
    }

    private void validateFirstName(String value, Consumer<ValidationError> listener) {
        if (value == null || value.isEmpty()) {
            listener.accept(error(RegistrationPage.FIELD_FIRST_NAME, Messages.MISSING_FIRST_NAME));
            return;
        }
        if (value.length() > 255 || isBlank(value)) {
            listener.accept(error(RegistrationPage.FIELD_FIRST_NAME, ExternalMessages.INVALID_FIRST_NAME));
        }
    }

    private void validateLastName(String value, Consumer<ValidationError> listener) {
        if (value == null || value.isEmpty()) {
            listener.accept(error(RegistrationPage.FIELD_LAST_NAME, Messages.MISSING_LAST_NAME));
            return;
        }
        if (value.length() > 255 || isBlank(value)) {
            listener.accept(error(RegistrationPage.FIELD_LAST_NAME, ExternalMessages.INVALID_LAST_NAME));
        }
    }

    private void validateMiddleName(String value, Consumer<ValidationError> listener) {
        if (value == null || value.isEmpty()) {
            return;
        }
        if (value.length() > 255 || isBlank(value)) {
            listener.accept(error(ExternalRegistrationPage.FIELD_MIDDLE_NAME, ExternalMessages.INVALID_MIDDLE_NAME));
        }
    }

    private boolean isBlank(String source) {
        return source.matches("\\s+");
    }

    private void validateClientType(String value, Consumer<ValidationError> listener) {
        if (value == null || value.isEmpty()) {
            listener.accept(error(ExternalRegistrationPage.FIELD_CLIENT_TYPE, ExternalMessages.MISSING_CLIENT_TYPE));
            return;
        }
        if (
                value.length() > 35
                        || (!value.equals(ExternalRegistrationPage.CLIENT_PHYSICAL)
                        && !value.equals(ExternalRegistrationPage.CLIENT_LEGAL))
        ) {
            listener.accept(error(ExternalRegistrationPage.FIELD_CLIENT_TYPE, ExternalMessages.INVALID_CLIENT_TYPE));
        }
    }

    private void validateBin(String value, Consumer<ValidationError> listener) {
        if (value == null || value.isEmpty()) {
            return;
        }
        if (value.length() != 12 || !value.matches("\\d+")) {
            listener.accept(error(ExternalRegistrationPage.FIELD_BIN, ExternalMessages.INVALID_BIN));
        }
    }

    private void validateLegalRole(String value, Consumer<ValidationError> listener) {
        if (value == null || value.isEmpty()) {
            return;
        }
        if (
                value.length() > 35
                        || (!value.equals(ExternalRegistrationPage.ROLE_HEAD)
                        && !value.equals(ExternalRegistrationPage.ROLE_EMPLOYEE))
        ) {
            listener.accept(error(ExternalRegistrationPage.FIELD_LEGAL_ROLE, ExternalMessages.INVALID_LEGAL_ROLE));
        }
    }

    private void validateResidency(String value, Consumer<ValidationError> listener) {
        if (value == null || value.isEmpty()) {
            listener.accept(error(ExternalRegistrationPage.FIELD_RESIDENCY, ExternalMessages.MISSING_RESIDENCY));
            return;
        }
        if (
                !value.equals(ExternalRegistrationPage.RESIDENT)
                    && !value.equals(ExternalRegistrationPage.NON_RESIDENT)
        ) {
            listener.accept(error(ExternalRegistrationPage.FIELD_RESIDENCY, ExternalMessages.INVALID_RESIDENCY));
        }
    }

    private void validatePhoneNumber(String value, Consumer<ValidationError> listener) {
        if (value == null || value.isEmpty()) {
            listener.accept(error(ExternalRegistrationPage.FIELD_PHONE_NUMBER, ExternalMessages.INVALID_PHONE_NUMBER));
            return;
        }
        
        PhoneNumberValidator.PhoneNumberValidationResult result = PhoneNumberValidator.validatePhoneNumber(value);
        
        if (!result.isValid()) {
            System.out.println("Phone validation failed for '" + value + "': " + result.getErrorMessage());
            
            if (result.getErrorMessage().contains("format")) {
                listener.accept(error(ExternalRegistrationPage.FIELD_PHONE_NUMBER, ExternalMessages.INVALID_PHONE_FORMAT));
            } else {
                listener.accept(error(ExternalRegistrationPage.FIELD_PHONE_NUMBER, ExternalMessages.INVALID_PHONE_NUMBER));
            }
            return;
        }
        
        System.out.println("Phone number validated successfully: " + value +
                          " (Type: " + result.getNumberType() + 
                          ", Country: " + result.getPhoneNumber().getCountryCode() + ")");
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
