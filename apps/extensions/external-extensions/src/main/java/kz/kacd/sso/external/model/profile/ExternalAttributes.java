package kz.kacd.sso.external.model.profile;

import kz.kacd.sso.external.model.page.ExternalRegistrationPage;
import org.keycloak.authentication.forms.RegistrationPage;

import java.util.Map;

public class ExternalAttributes {

    private final Map<String, String> attributes;

    public ExternalAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    Map<String, String> getAttributes() {
        return attributes;
    }

    public void add(Map<String, String> newFields) {
        attributes.putAll(newFields);
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

    public String locale() {
        return attributes.get(ExternalRegistrationPage.FIELD_LOCALE);
    }

    public String phoneNumber() {
        String result = attributes.get(ExternalRegistrationPage.FIELD_PHONE_NUMBER);
        if (result != null) {
            result = result.replace(" ", "")
                    .replace("(", "")
                    .replace(")", "");
        }
        return result;
    }

    public String eds() {
        return attributes.get(ExternalRegistrationPage.FIELD_EDS);
    }

    public String orgName() {
        return attributes.get(ExternalRegistrationPage.FIELD_ORG_NAME);
    }
}
