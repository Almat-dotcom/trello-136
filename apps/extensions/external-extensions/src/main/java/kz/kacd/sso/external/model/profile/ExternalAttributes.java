package kz.kacd.sso.external.model.profile;

import kz.kacd.sso.external.model.page.ExternalRegistrationPage;
import org.keycloak.authentication.forms.RegistrationPage;

import java.util.Map;

public class ExternalAttributes {

    private final Map<String, String> attributes;

    public ExternalAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public String email() {
        return attributes.get(RegistrationPage.FIELD_EMAIL);
    }

    public String username() {
        if (residency() == null || residency().equals(ExternalRegistrationPage.NON_RESIDENT)) {
            return email().toLowerCase();
        }
        return iin() + "-" + clientType();
    }

    public String iin() {
        return attributes.get(ExternalRegistrationPage.FIELD_IIN);
    }

    public String firstName() {
        return attributes.get(RegistrationPage.FIELD_FIRST_NAME);
    }

    public String lastName() {
        return attributes.get(RegistrationPage.FIELD_LAST_NAME);
    }

    public String middleName() {
        return attributes.get(ExternalRegistrationPage.FIELD_MIDDLE_NAME);
    }

    public String clientType() {
        return attributes.get(ExternalRegistrationPage.FIELD_CLIENT_TYPE);
    }

    public String bin() {
        return attributes.get(ExternalRegistrationPage.FIELD_BIN);
    }

    public String legalRole() {
        return attributes.get(ExternalRegistrationPage.FIELD_LEGAL_ROLE);
    }

    public String residency() {
        return attributes.get(ExternalRegistrationPage.FIELD_RESIDENCY);
    }
}
