package kz.kacd.sso.external.model.profile;

import static kz.kacd.sso.external.model.profile.TestProfileFactory.originalForm;
import static kz.kacd.sso.external.model.profile.TestProfileFactory.validAppendedLegalResidentHead;
import static kz.kacd.sso.external.model.profile.TestProfileFactory.validLegalNonResidentHead;
import static kz.kacd.sso.external.model.profile.TestProfileFactory.validPhysicalNonResident;
import static kz.kacd.sso.external.model.profile.TestProfileFactory.validPhysicalResident;
import static org.assertj.core.api.Assertions.assertThat;

import jakarta.ws.rs.core.MultivaluedMap;
import kz.kacd.sso.external.model.page.ExternalRegistrationPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.models.UserModel;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ExternalAttributesTest {
    @Mock
    UserModel user;
    @InjectMocks
    ExternalUserProfileProvider profileProvider;

    @Test
    void should_return_correct_username_for_non_resident_physical() {
        ExternalAttributes expected = validPhysicalNonResident();
        MultivaluedMap<String, String> formData = originalForm(expected);
        ExternalUserProfile profile = profileProvider.create(formData, user);

        assertThat(profile.username())
            .isEqualTo(expected.email().toLowerCase());
    }

    @Test
    void should_return_correct_username_for_non_resident_legal() {
        ExternalAttributes expected = validLegalNonResidentHead();
        MultivaluedMap<String, String> formData = originalForm(expected);
        ExternalUserProfile profile = profileProvider.create(formData, user);

        assertThat(profile.username())
            .isEqualTo(expected.iin() + "-" + expected.bin() + "-" + ExternalRegistrationPage.CLIENT_LEGAL);
    }

    @Test
    void should_return_correct_username_for_resident_legal() {
        ExternalAttributes expected = validAppendedLegalResidentHead();
        MultivaluedMap<String, String> formData = originalForm(expected);
        ExternalUserProfile profile = profileProvider.create(formData, user);

        assertThat(profile.username())
            .isEqualTo(expected.iin() + "-" + expected.bin() + "-" + ExternalRegistrationPage.CLIENT_LEGAL);
    }

    @Test
    void should_return_correct_username_for_resident_physical() {
        ExternalAttributes expected = validPhysicalResident();
        MultivaluedMap<String, String> formData = originalForm(expected);
        ExternalUserProfile profile = profileProvider.create(formData, user);

        assertThat(profile.username())
            .isEqualTo(expected.iin() + "-" + ExternalRegistrationPage.CLIENT_PHYSICAL);
    }
}
