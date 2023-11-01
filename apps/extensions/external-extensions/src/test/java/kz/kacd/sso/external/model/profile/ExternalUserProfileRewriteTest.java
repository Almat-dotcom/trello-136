package kz.kacd.sso.external.model.profile;

import jakarta.ws.rs.core.MultivaluedMap;
import kz.kacd.sso.external.sign.SignatureSubject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.authentication.forms.RegistrationPage;
import org.keycloak.models.KeycloakSession;
import org.keycloak.sessions.AuthenticationSessionModel;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static kz.kacd.sso.external.model.profile.TestProfileFactory.originalForm;
import static kz.kacd.sso.external.model.profile.TestProfileFactory.validPhysicalResident;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
class ExternalUserProfileRewriteTest {

    @Mock
    KeycloakSession session;
    @Mock
    AuthenticationSessionModel auth;
    @InjectMocks
    ExternalUserProfileProvider provider;

    @Test
    void should_rewrite_attribute_from_auth() {
        MultivaluedMap<String, String> formData = originalForm(validPhysicalResident());
        String newFirstName = "My New FirstName";
        given(auth.getAuthNote(anyString())).will(a -> {
            if (a.getArguments()[0].equals(RegistrationPage.FIELD_FIRST_NAME)) {
                return newFirstName;
            }
            return null;
        });
        ExternalUserProfile profile = provider.create(formData);

        profile.rewriteFromSession(auth);

        assertThat(profile.firstName()).isEqualTo(newFirstName);
    }

    @Test
    void should_rewrite_attribute_from_subject() {
        MultivaluedMap<String, String> formData = originalForm(validPhysicalResident());
        SignatureSubject subject = new SignatureSubject();
        subject.setSurName("Test");
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        doNothing().when(auth).setAuthNote(anyString(), captor.capture());
        given(auth.getAuthNote(anyString())).will(a -> {
            if (a.getArguments()[0].equals(RegistrationPage.FIELD_LAST_NAME)) {
                return captor.getValue();
            }
            return null;
        });
        ExternalUserProfile profile = provider.create(formData);

        profile.rewriteFromSubject(subject, auth);

        assertThat(profile.lastName()).isEqualTo(subject.getSurName());
        then(auth).should().setAuthNote(RegistrationPage.FIELD_LAST_NAME, subject.getSurName());
    }
}
