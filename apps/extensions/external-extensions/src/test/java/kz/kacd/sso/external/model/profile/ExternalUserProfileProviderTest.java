package kz.kacd.sso.external.model.profile;

import kz.kacd.sso.external.model.page.ExternalRegistrationPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.UserModel;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.ws.rs.core.MultivaluedMap;

import static kz.kacd.sso.external.model.profile.TestProfileFactory.originalForm;
import static kz.kacd.sso.external.model.profile.TestProfileFactory.validPhysicalResident;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ExternalUserProfileProviderTest {

    @Mock
    KeycloakSession keycloakSession;
    @Mock
    UserModel user;
    @InjectMocks
    ExternalUserProfileProvider provider;

    @Test
    void should_create_new_profile() {
        MultivaluedMap<String, String> form = originalForm(validPhysicalResident());

        ExternalUserProfile profile = provider.create(form);

        assertThat(profile.iin()).isEqualTo(form.getFirst(ExternalRegistrationPage.FIELD_IIN));
        assertThat(profile.residency()).isEqualTo(form.getFirst(ExternalRegistrationPage.FIELD_RESIDENCY));
        assertThat(profile.clientType()).isEqualTo(form.getFirst(ExternalRegistrationPage.FIELD_CLIENT_TYPE));
    }

    @Test
    void should_extract_profile() {
        MultivaluedMap<String, String> form = originalForm(validPhysicalResident());

        ExternalUserProfile profile = provider.create(form, user);

        assertThat(profile.iin()).isEqualTo(form.getFirst(ExternalRegistrationPage.FIELD_IIN));
        assertThat(profile.residency()).isEqualTo(form.getFirst(ExternalRegistrationPage.FIELD_RESIDENCY));
        assertThat(profile.clientType()).isEqualTo(form.getFirst(ExternalRegistrationPage.FIELD_CLIENT_TYPE));
    }
}