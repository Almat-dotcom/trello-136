package kz.kacd.sso.external.model.profile;

import kz.kacd.sso.external.model.OrganizationModel;
import kz.kacd.sso.external.model.OrganizationProvider;
import kz.kacd.sso.external.model.PositionModel;
import kz.kacd.sso.external.model.page.ExternalRegistrationPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.authentication.forms.RegistrationPage;
import org.keycloak.models.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class ExternalUserProfileProviderTest {

    @Mock
    KeycloakSession session;
    @Mock
    KeycloakContext context;
    @Mock
    UserProvider users;
    @Mock
    OrganizationProvider orgs;
    @InjectMocks
    ExternalUserProfileProvider provider;

    @Test
    void should_correctly_parse_base_form_attributes() {
        String email = "test@example.com";
        String username = "test";
        String firstName = "My";
        String lastName = "Test";
        MultivaluedMap<String, String> formData = toForm(email, username, firstName, lastName);

        ExternalUserProfile profile = provider.create(formData);

        assertThat(profile.email()).isEqualTo(email);
        assertThat(profile.username()).isEqualTo(username + "-" + profile.clientType());
        assertThat(profile.firstName()).isEqualTo(firstName);
        assertThat(profile.lastName()).isEqualTo(lastName);
    }

    @Test
    void should_create_new_user() {
        MultivaluedMap<String, String> formData = physicalClient();
        given(session.users()).willReturn(users);
        given(
                users.addUser(
                        any(),
                        eq(formData.getFirst(ExternalRegistrationPage.FIELD_IIN) + "-" + formData.getFirst(ExternalRegistrationPage.FIELD_CLIENT_TYPE))
                )
        ).willReturn(mock(UserModel.class));
        given(session.getContext()).willReturn(context);
        given(context.getRealm()).willReturn(mock(RealmModel.class));

        ExternalUserProfile profile = provider.create(formData);
        profile.create();

        then(users).should().addUser(
                any(),
                eq(formData.getFirst(ExternalRegistrationPage.FIELD_IIN) + "-" + formData.getFirst(ExternalRegistrationPage.FIELD_CLIENT_TYPE))
        );
    }

    @Test
    void should_initialize_all_user_model_attributes() {
        MultivaluedMap<String, String> formData = physicalClient();
        UserModel target = mock(UserModel.class);
        given(session.users()).willReturn(users);
        given(
                users.addUser(
                        any(),
                        eq(formData.getFirst(ExternalRegistrationPage.FIELD_IIN) + "-" + formData.getFirst(ExternalRegistrationPage.FIELD_CLIENT_TYPE))
                )
        ).willReturn(target);
        given(session.getContext()).willReturn(context);
        given(context.getRealm()).willReturn(mock(RealmModel.class));

        ExternalUserProfile profile = provider.create(formData);
        profile.create();

        then(target).should().setEmail(any());
        then(target).should().setFirstName(any());
        then(target).should().setLastName(any());
        then(target).should().setAttribute(eq(ExternalRegistrationPage.FIELD_CLIENT_TYPE), any());
    }

    @Test
    void should_initialize_new_org() {
        MultivaluedMap<String, String> formData = legalClient(ExternalRegistrationPage.ROLE_HEAD);
        given(session.users()).willReturn(users);
        given(
                users.addUser(
                        any(),
                        eq(formData.getFirst(ExternalRegistrationPage.FIELD_IIN) + "-" + formData.getFirst(ExternalRegistrationPage.FIELD_CLIENT_TYPE))
                )
        ).willReturn(mock(UserModel.class));
        given(session.getContext()).willReturn(context);
        given(context.getRealm()).willReturn(mock(RealmModel.class));
        given(session.getProvider(OrganizationProvider.class)).willReturn(orgs);
        given(orgs.getOrganizationByBin(any(), any())).willReturn(null);
        given(orgs.createOrganization(any(), any())).willReturn(mock(OrganizationModel.class));

        provider.create(formData).create();

        then(orgs).should().createOrganization(any(), any());
    }

    @Test
    void should_reject_creation_of_org_if_role_is_not_head() {
        MultivaluedMap<String, String> formData = legalClient(ExternalRegistrationPage.ROLE_EMPLOYEE);
        given(session.users()).willReturn(users);
        given(
                users.addUser(
                        any(),
                        eq(formData.getFirst(ExternalRegistrationPage.FIELD_IIN) + "-" + formData.getFirst(ExternalRegistrationPage.FIELD_CLIENT_TYPE))
                )
        ).willReturn(mock(UserModel.class));
        given(session.getContext()).willReturn(context);
        given(context.getRealm()).willReturn(mock(RealmModel.class));
        given(session.getProvider(OrganizationProvider.class)).willReturn(orgs);
        given(orgs.getOrganizationByBin(any(), any())).willReturn(null);

        ExternalUserProfile profile = provider.create(formData);

        assertThatThrownBy(profile::create).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void should_request_position_for_employee() {
        OrganizationModel org = mock(OrganizationModel.class);
        given(org.getPosition(any(UserModel.class))).willReturn(null);
        MultivaluedMap<String, String> formData = legalClient(ExternalRegistrationPage.ROLE_EMPLOYEE);
        given(session.users()).willReturn(users);
        given(
                users.addUser(
                        any(),
                        eq(formData.getFirst(ExternalRegistrationPage.FIELD_IIN) + "-" + formData.getFirst(ExternalRegistrationPage.FIELD_CLIENT_TYPE))
                )
        ).willReturn(mock(UserModel.class));
        given(session.getContext()).willReturn(context);
        given(context.getRealm()).willReturn(mock(RealmModel.class));
        given(session.getProvider(OrganizationProvider.class)).willReturn(orgs);
        given(orgs.getOrganizationByBin(any(), any())).willReturn(org);

        provider.create(formData).create();

        then(org).should().requestPosition(eq(PositionModel.EMPLOYEE), any());
    }

    private MultivaluedMap<String, String> physicalClient() {
        MultivaluedMap<String, String> result =
                toForm("test@example.com", "111111111111", "My", "Test");
        result.put(
                ExternalRegistrationPage.FIELD_CLIENT_TYPE,
                Collections.singletonList(ExternalRegistrationPage.CLIENT_PHYSICAL)
        );
        return result;
    }

    private MultivaluedMap<String, String> legalClient(String role) {
        MultivaluedMap<String, String> result =
                toForm("test@example.com", "111111111111", "My", "Test");
        result.put(
                ExternalRegistrationPage.FIELD_CLIENT_TYPE,
                Collections.singletonList(ExternalRegistrationPage.CLIENT_LEGAL)
        );
        result.put(
                ExternalRegistrationPage.FIELD_BIN,
                Collections.singletonList("111111111111")
        );
        result.put(
                ExternalRegistrationPage.FIELD_LEGAL_ROLE,
                Collections.singletonList(role)
        );
        return result;
    }

    private MultivaluedMap<String, String> toForm(String email, String username, String firstName, String lastName) {
        MultivaluedMap<String, String> result = new MultivaluedHashMap<>();
        result.put(RegistrationPage.FIELD_EMAIL, Collections.singletonList(email));
        result.put(ExternalRegistrationPage.FIELD_IIN, Collections.singletonList(username));
        result.put(RegistrationPage.FIELD_FIRST_NAME, Collections.singletonList(firstName));
        result.put(RegistrationPage.FIELD_LAST_NAME, Collections.singletonList(lastName));
        return result;
    }

}