package kz.kacd.sso.external.model.page;

import org.keycloak.authentication.forms.RegistrationPage;
import org.keycloak.forms.login.LoginFormsProvider;

/**
 * Extended registration page for external realm.
 */
public class ExternalRegistrationPage extends RegistrationPage {

    public static final String FIELD_CLIENT_TYPE = "clientType";
    public static final String FIELD_LEGAL_ROLE = "legalRole";
    public static final String FIELD_MIDDLE_NAME = "middleName";
    public static final String FIELD_BIN = "bin";
    public static final String FIELD_IIN = "iin";
    public static final String FIELD_RESIDENCY = "residency";
    public static final String FIELD_LOCALE = "locale";
    public static final String FIELD_PHONE_NUMBER = "phoneNumber";

    public static final String CLIENT_PHYSICAL = "physical";
    public static final String CLIENT_LEGAL = "legal";

    public static final String ROLE_HEAD = "head";
    public static final String ROLE_EMPLOYEE = "employee";

    public static final String RESIDENT = "resident";
    public static final String NON_RESIDENT = "non-resident";

    private final LoginFormsProvider form;

    private ExternalRegistrationPage(LoginFormsProvider form) {
        this.form = form;
    }

    public static ExternalRegistrationPage from(LoginFormsProvider form) {
        return new ExternalRegistrationPage(form);
    }

    public void configure() {
        form.setAttribute(FIELD_CLIENT_TYPE, "");
        form.setAttribute(FIELD_LEGAL_ROLE, "");
        form.setAttribute(FIELD_MIDDLE_NAME, "");
        form.setAttribute(FIELD_BIN, "");
        form.setAttribute(FIELD_IIN, "");
        form.setAttribute(FIELD_RESIDENCY, "");
    }
}
