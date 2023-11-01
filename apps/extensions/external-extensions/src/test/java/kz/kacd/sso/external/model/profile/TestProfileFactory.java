package kz.kacd.sso.external.model.profile;

import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import kz.kacd.sso.external.model.page.ExternalRegistrationPage;
import org.keycloak.authentication.forms.RegistrationPage;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TestProfileFactory {

    public static MultivaluedMap<String, String> originalForm(ExternalAttributes attributes) {
        MultivaluedMap<String, String> result = new MultivaluedHashMap<>();
        attributes.getAttributes().forEach((k, v) -> result.put(k, Collections.singletonList(v)));
        return result;
    }

    public static ExternalAttributes validPhysicalResident() {
        Map<String, String> result = new HashMap<>();
        result.put(ExternalRegistrationPage.FIELD_RESIDENCY, ExternalRegistrationPage.RESIDENT);
        result.put(ExternalRegistrationPage.FIELD_CLIENT_TYPE, ExternalRegistrationPage.CLIENT_PHYSICAL);
        result.put(RegistrationPage.FIELD_LAST_NAME, "test");
        result.put(RegistrationPage.FIELD_FIRST_NAME, "test");
        result.put(ExternalRegistrationPage.FIELD_MIDDLE_NAME, "test");
        result.put(ExternalRegistrationPage.FIELD_PHONE_NUMBER, "+7 (777) 777 77 77");
        result.put(ExternalRegistrationPage.FIELD_EMAIL, "test@test.com");
        result.put(ExternalRegistrationPage.FIELD_IIN, "123456789012");
        result.put(ExternalRegistrationPage.FIELD_PASSWORD, "testPassword");
        result.put(ExternalRegistrationPage.FIELD_PASSWORD_CONFIRM, "testPassword");
        return new ExternalAttributes(result);
    }

    public static ExternalAttributes validPhysicalNonResident() {
        Map<String, String> result = new HashMap<>();
        result.put(ExternalRegistrationPage.FIELD_RESIDENCY, ExternalRegistrationPage.NON_RESIDENT);
        result.put(ExternalRegistrationPage.FIELD_CLIENT_TYPE, ExternalRegistrationPage.CLIENT_PHYSICAL);
        result.put(RegistrationPage.FIELD_LAST_NAME, "test");
        result.put(RegistrationPage.FIELD_FIRST_NAME, "test");
        result.put(ExternalRegistrationPage.FIELD_MIDDLE_NAME, "test");
        result.put(ExternalRegistrationPage.FIELD_PHONE_NUMBER, "+7 (777) 777 77 77");
        result.put(ExternalRegistrationPage.FIELD_EMAIL, "test@test.com");
        result.put(ExternalRegistrationPage.FIELD_PASSWORD, "testPassword");
        result.put(ExternalRegistrationPage.FIELD_PASSWORD_CONFIRM, "testPassword");
        return new ExternalAttributes(result);
    }

    public static ExternalAttributes validLegalResident() {
        Map<String, String> result = new HashMap<>();
        result.put(ExternalRegistrationPage.FIELD_RESIDENCY, ExternalRegistrationPage.RESIDENT);
        result.put(ExternalRegistrationPage.FIELD_CLIENT_TYPE, ExternalRegistrationPage.CLIENT_LEGAL);
        result.put(ExternalRegistrationPage.FIELD_PHONE_NUMBER, "+7 (777) 777 77 77");
        result.put(ExternalRegistrationPage.FIELD_EMAIL, "test@test.com");
        result.put(ExternalRegistrationPage.FIELD_PASSWORD, "testPassword");
        result.put(ExternalRegistrationPage.FIELD_PASSWORD_CONFIRM, "testPassword");
        result.put(ExternalRegistrationPage.FIELD_EDS, "some signed XML");
        return new ExternalAttributes(result);
    }

    public static ExternalAttributes validAppendedLegalResidentHead() {
        Map<String, String> result = new HashMap<>();
        result.put(ExternalRegistrationPage.FIELD_RESIDENCY, ExternalRegistrationPage.RESIDENT);
        result.put(ExternalRegistrationPage.FIELD_CLIENT_TYPE, ExternalRegistrationPage.CLIENT_LEGAL);
        result.put(ExternalRegistrationPage.FIELD_LEGAL_ROLE, ExternalRegistrationPage.ROLE_HEAD);
        result.put(ExternalRegistrationPage.FIELD_BIN, "123456789012");
        result.put(ExternalRegistrationPage.FIELD_IIN, "123456789012");
        result.put(RegistrationPage.FIELD_LAST_NAME, "test");
        result.put(RegistrationPage.FIELD_FIRST_NAME, "test");
        result.put(ExternalRegistrationPage.FIELD_MIDDLE_NAME, "test");
        result.put(ExternalRegistrationPage.FIELD_PHONE_NUMBER, "+7 (777) 777 77 77");
        result.put(ExternalRegistrationPage.FIELD_EMAIL, "test@test.com");
        result.put(ExternalRegistrationPage.FIELD_PASSWORD, "testPassword");
        result.put(ExternalRegistrationPage.FIELD_PASSWORD_CONFIRM, "testPassword");
        result.put(ExternalRegistrationPage.FIELD_EDS, "some signed XML");
        return new ExternalAttributes(result);
    }

    public static ExternalAttributes validAppendedLegalResidentEmployee() {
        Map<String, String> result = new HashMap<>();
        result.put(ExternalRegistrationPage.FIELD_RESIDENCY, ExternalRegistrationPage.RESIDENT);
        result.put(ExternalRegistrationPage.FIELD_CLIENT_TYPE, ExternalRegistrationPage.CLIENT_LEGAL);
        result.put(ExternalRegistrationPage.FIELD_LEGAL_ROLE, ExternalRegistrationPage.ROLE_EMPLOYEE);
        result.put(ExternalRegistrationPage.FIELD_BIN, "123456789012");
        result.put(ExternalRegistrationPage.FIELD_IIN, "123456789012");
        result.put(RegistrationPage.FIELD_LAST_NAME, "test");
        result.put(RegistrationPage.FIELD_FIRST_NAME, "test");
        result.put(ExternalRegistrationPage.FIELD_MIDDLE_NAME, "test");
        result.put(ExternalRegistrationPage.FIELD_PHONE_NUMBER, "+7 (777) 777 77 77");
        result.put(ExternalRegistrationPage.FIELD_EMAIL, "test@test.com");
        result.put(ExternalRegistrationPage.FIELD_PASSWORD, "testPassword");
        result.put(ExternalRegistrationPage.FIELD_PASSWORD_CONFIRM, "testPassword");
        result.put(ExternalRegistrationPage.FIELD_EDS, "some signed XML");
        return new ExternalAttributes(result);
    }

    public static ExternalAttributes validLegalNonResidentHead() {
        Map<String, String> result = new HashMap<>();
        result.put(ExternalRegistrationPage.FIELD_RESIDENCY, ExternalRegistrationPage.NON_RESIDENT);
        result.put(ExternalRegistrationPage.FIELD_CLIENT_TYPE, ExternalRegistrationPage.CLIENT_LEGAL);
        result.put(ExternalRegistrationPage.FIELD_LEGAL_ROLE, ExternalRegistrationPage.ROLE_HEAD);
        result.put(RegistrationPage.FIELD_LAST_NAME, "test");
        result.put(RegistrationPage.FIELD_FIRST_NAME, "test");
        result.put(ExternalRegistrationPage.FIELD_MIDDLE_NAME, "test");
        result.put(ExternalRegistrationPage.FIELD_PHONE_NUMBER, "+7 (777) 777 77 77");
        result.put(ExternalRegistrationPage.FIELD_EMAIL, "test@test.com");
        result.put(ExternalRegistrationPage.FIELD_PASSWORD, "testPassword");
        result.put(ExternalRegistrationPage.FIELD_PASSWORD_CONFIRM, "testPassword");
        return new ExternalAttributes(result);
    }

    public static ExternalAttributes validLegalNonResidentEmployee() {
        Map<String, String> result = new HashMap<>();
        result.put(ExternalRegistrationPage.FIELD_RESIDENCY, ExternalRegistrationPage.NON_RESIDENT);
        result.put(ExternalRegistrationPage.FIELD_CLIENT_TYPE, ExternalRegistrationPage.CLIENT_LEGAL);
        result.put(ExternalRegistrationPage.FIELD_LEGAL_ROLE, ExternalRegistrationPage.ROLE_EMPLOYEE);
        result.put(RegistrationPage.FIELD_LAST_NAME, "test");
        result.put(RegistrationPage.FIELD_FIRST_NAME, "test");
        result.put(ExternalRegistrationPage.FIELD_MIDDLE_NAME, "test");
        result.put(ExternalRegistrationPage.FIELD_PHONE_NUMBER, "+7 (777) 777 77 77");
        result.put(ExternalRegistrationPage.FIELD_EMAIL, "test@test.com");
        result.put(ExternalRegistrationPage.FIELD_PASSWORD, "testPassword");
        result.put(ExternalRegistrationPage.FIELD_PASSWORD_CONFIRM, "testPassword");
        result.put(ExternalRegistrationPage.FIELD_BIN, "NR0000000123");
        return new ExternalAttributes(result);
    }
}
