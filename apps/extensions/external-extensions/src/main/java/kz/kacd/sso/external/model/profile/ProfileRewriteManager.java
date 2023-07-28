package kz.kacd.sso.external.model.profile;

import kz.kacd.sso.external.model.page.ExternalRegistrationPage;
import kz.kacd.sso.external.sign.SignatureSubject;
import org.keycloak.authentication.forms.RegistrationPage;
import org.keycloak.sessions.AuthenticationSessionModel;

import java.util.HashMap;
import java.util.Map;

import static org.keycloak.utils.StringUtil.isNotBlank;

public class ProfileRewriteManager {

    private final ExternalAttributes attributes;

    ProfileRewriteManager(ExternalAttributes attributes) {
        this.attributes = attributes;
    }

    void rewriteFromSubject(SignatureSubject subject, AuthenticationSessionModel auth) {
        if (isNotBlank(subject.getIin())) {
            auth.setAuthNote(ExternalRegistrationPage.FIELD_IIN, subject.getIin());
        }
        if (isNotBlank(subject.getBin())) {
            auth.setAuthNote(ExternalRegistrationPage.FIELD_BIN, subject.getBin());
        }
        if (isNotBlank(subject.getFirstName())) {
            auth.setAuthNote(RegistrationPage.FIELD_FIRST_NAME, subject.getFirstName());
        }
        if (isNotBlank(subject.getSurName())) {
            auth.setAuthNote(RegistrationPage.FIELD_LAST_NAME, subject.getSurName());
        }
        if (isNotBlank(subject.getGivenName())) {
            auth.setAuthNote(ExternalRegistrationPage.FIELD_MIDDLE_NAME, subject.getGivenName());
        }
        if (subject.getExtendedKeyUsage() != null) {
            auth.setAuthNote(
                    ExternalRegistrationPage.FIELD_LEGAL_ROLE,
                    subject.ceo() ? ExternalRegistrationPage.ROLE_HEAD : ExternalRegistrationPage.ROLE_EMPLOYEE
            );
            auth.setAuthNote(
                    ExternalRegistrationPage.FIELD_CLIENT_TYPE,
                    subject.legal() ? ExternalRegistrationPage.CLIENT_LEGAL : ExternalRegistrationPage.CLIENT_PHYSICAL
            );
        }
        rewriteFromSession(auth);
    }

    void rewriteFromSession(AuthenticationSessionModel auth) {
        Map<String, String> overwrites = new HashMap<>();
        if (isNotBlank(auth.getAuthNote(ExternalRegistrationPage.FIELD_IIN))) {
            overwrites.put(ExternalRegistrationPage.FIELD_IIN, auth.getAuthNote(ExternalRegistrationPage.FIELD_IIN));
        }
        if (isNotBlank(auth.getAuthNote(ExternalRegistrationPage.FIELD_BIN))) {
            overwrites.put(ExternalRegistrationPage.FIELD_BIN, auth.getAuthNote(ExternalRegistrationPage.FIELD_BIN));
        }
        if (isNotBlank(auth.getAuthNote(RegistrationPage.FIELD_FIRST_NAME))) {
            overwrites.put(RegistrationPage.FIELD_FIRST_NAME, auth.getAuthNote(RegistrationPage.FIELD_FIRST_NAME));
        }
        if (isNotBlank(auth.getAuthNote(RegistrationPage.FIELD_LAST_NAME))) {
            overwrites.put(RegistrationPage.FIELD_LAST_NAME, auth.getAuthNote(RegistrationPage.FIELD_LAST_NAME));
        }
        if (isNotBlank(auth.getAuthNote(ExternalRegistrationPage.FIELD_MIDDLE_NAME))) {
            overwrites.put(ExternalRegistrationPage.FIELD_MIDDLE_NAME, auth.getAuthNote(ExternalRegistrationPage.FIELD_MIDDLE_NAME));
        }
        if (isNotBlank(auth.getAuthNote(ExternalRegistrationPage.FIELD_LEGAL_ROLE))) {
            overwrites.put(ExternalRegistrationPage.FIELD_LEGAL_ROLE, auth.getAuthNote(ExternalRegistrationPage.FIELD_LEGAL_ROLE));
        }
        if (isNotBlank(auth.getAuthNote(ExternalRegistrationPage.FIELD_CLIENT_TYPE))) {
            overwrites.put(ExternalRegistrationPage.FIELD_CLIENT_TYPE, auth.getAuthNote(ExternalRegistrationPage.FIELD_CLIENT_TYPE));
        }
        attributes.add(overwrites);
    }
}
