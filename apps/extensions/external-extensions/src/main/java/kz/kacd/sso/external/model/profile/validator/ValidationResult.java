package kz.kacd.sso.external.model.profile.validator;

import org.keycloak.userprofile.ValidationException;

import java.util.Map;

public class ValidationResult {

    private final ValidationException exception;
    private final Map<String, String> additionalAttributes;

    public ValidationResult(ValidationException exception, Map<String, String> additionalAttributes) {
        this.exception = exception;
        this.additionalAttributes = additionalAttributes;
    }

    public ValidationException getException() {
        return exception;
    }

    public Map<String, String> getAdditionalAttributes() {
        return additionalAttributes;
    }
}
