package kz.kacd.sso.external.model.profile;

import jakarta.ws.rs.core.MultivaluedMap;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.models.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static kz.kacd.sso.external.model.profile.TestProfileFactory.originalForm;
import static kz.kacd.sso.external.model.profile.TestProfileFactory.validPhysicalResident;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class ExternalUserProfileCreationTest {

    @Mock
    KeycloakSession session;
    @Mock
    UserProvider userProvider;
    @Mock
    UserModel user;
    @Mock
    KeycloakContext context;
    @Mock
    RealmModel realm;
    @InjectMocks
    ExternalUserProfileProvider provider;

    @Test
    void should_create_new_user() {
        MultivaluedMap<String, String> formData = originalForm(validPhysicalResident());
        given(session.getContext()).willReturn(context);
        given(context.getRealm()).willReturn(realm);
        given(session.users()).willReturn(userProvider);
        given(userProvider.addUser(eq(realm), any())).willReturn(user);
        ExternalUserProfile profile = provider.create(formData);

        UserModel created = profile.create();

        assertThat(created).isEqualTo(user);
        then(userProvider).should().addUser(eq(realm), any());
    }

    @Test
    void should_not_create_new_user() {
        MultivaluedMap<String, String> formData = originalForm(validPhysicalResident());
        ExternalUserProfile profile = provider.create(formData, user);

        UserModel created = profile.create();

        assertThat(created).isEqualTo(user);
        then(userProvider).should(never()).addUser(eq(realm), any());
    }
}