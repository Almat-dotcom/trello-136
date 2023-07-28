package kz.kacd.sso.external.model.profile;

import kz.kacd.sso.external.model.OrganizationModel;
import kz.kacd.sso.external.model.OrganizationProvider;
import kz.kacd.sso.external.model.PositionModel;
import kz.kacd.sso.external.model.page.ExternalRegistrationPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.models.KeycloakContext;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.ws.rs.core.MultivaluedMap;
import java.util.*;
import java.util.stream.Stream;

import static kz.kacd.sso.external.model.profile.TestProfileFactory.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class ExternalUserProfileUpdateTest {

    @Mock
    KeycloakSession keycloakSession;
    @Mock
    UserModel user;
    @Mock
    OrganizationProvider organizationProvider;
    @Mock
    OrganizationModel org;
    @Mock
    PositionModel position;
    @Mock
    KeycloakContext context;
    @Mock
    RealmModel realm;
    @InjectMocks
    ExternalUserProfileProvider profileProvider;

    @Test
    void should_update_user_fields() {
        ExternalAttributes expected = validPhysicalResident();
        MultivaluedMap<String, String> formData = originalForm(expected);
        ExternalUserProfile profile = profileProvider.create(formData, user);

        profile.update();

        then(user).should().setEmail(expected.email());
        then(user).should().setLastName(expected.lastName());
        then(user).should().setFirstName(expected.firstName());
    }

    @Test
    void should_update_attributes() {
        ExternalAttributes expected = validPhysicalResident();
        MultivaluedMap<String, String> formData = originalForm(expected);
        ExternalUserProfile profile = profileProvider.create(formData, user);

        profile.update();

        then(user).should().setSingleAttribute(ExternalRegistrationPage.FIELD_MIDDLE_NAME, expected.middleName());
        then(user).should().setSingleAttribute(ExternalRegistrationPage.FIELD_CLIENT_TYPE, expected.clientType());
        then(user).should().setSingleAttribute(ExternalRegistrationPage.FIELD_RESIDENCY, expected.residency());
        then(user).should().setSingleAttribute(ExternalRegistrationPage.FIELD_IIN, expected.iin());
        then(user).should().setSingleAttribute(ExternalRegistrationPage.FIELD_PHONE_NUMBER, expected.phoneNumber());
    }

    @Test
    void should_set_phone_number_verified() {
        ExternalAttributes expected = validPhysicalResident();
        MultivaluedMap<String, String> formData = originalForm(expected);
        given(user.getAttributes()).willReturn(Collections.emptyMap());
        ExternalUserProfile profile = profileProvider.create(formData, user);

        profile.update();

        then(user).should().setSingleAttribute(ExternalRegistrationPage.FIELD_PHONE_VERIFIED, "false");
    }

    @Test
    void should_not_update_phone_number_verified() {
        ExternalAttributes expected = validPhysicalResident();
        MultivaluedMap<String, String> formData = originalForm(expected);
        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put(ExternalRegistrationPage.FIELD_PHONE_VERIFIED, Collections.singletonList("true"));
        given(user.getAttributes()).willReturn(attributes);
        ExternalUserProfile profile = profileProvider.create(formData, user);

        profile.update();

        then(user).should(never()).setSingleAttribute(ExternalRegistrationPage.FIELD_PHONE_VERIFIED, "false");
    }

    @Test
    void should_find_org_by_bin_for_residents() {
        ExternalAttributes expected = validAppendedLegalResidentEmployee();
        MultivaluedMap<String, String> formData = originalForm(expected);
        given(keycloakSession.getContext()).willReturn(context);
        given(context.getRealm()).willReturn(realm);
        given(keycloakSession.getProvider(OrganizationProvider.class)).willReturn(organizationProvider);
        given(organizationProvider.getOrganizationByBin(realm, expected.bin())).willReturn(org);
        given(org.getPosition(user)).willReturn(position);
        given(position.getName()).willReturn(expected.legalRole().toUpperCase());
        ExternalUserProfile profile = profileProvider.create(formData, user);

        profile.update();

        then(organizationProvider).should().getOrganizationByBin(realm, expected.bin());
    }

    @Test
    void should_find_org_by_bin_for_non_resident_employee() {
        ExternalAttributes expected = validLegalNonResidentEmployee();
        MultivaluedMap<String, String> formData = originalForm(expected);
        given(keycloakSession.getContext()).willReturn(context);
        given(context.getRealm()).willReturn(realm);
        given(keycloakSession.getProvider(OrganizationProvider.class)).willReturn(organizationProvider);
        given(organizationProvider.getOrganizationByBin(realm, expected.bin())).willReturn(org);
        given(org.getPosition(user)).willReturn(position);
        ExternalUserProfile profile = profileProvider.create(formData, user);

        profile.update();

        then(organizationProvider).should().getOrganizationByBin(realm, expected.bin());
    }

    @Test
    void should_find_org_by_user_for_non_resident_head() {
        ExternalAttributes expected = validLegalNonResidentHead();
        MultivaluedMap<String, String> formData = originalForm(expected);
        given(keycloakSession.getContext()).willReturn(context);
        given(context.getRealm()).willReturn(realm);
        given(keycloakSession.getProvider(OrganizationProvider.class)).willReturn(organizationProvider);
        given(organizationProvider.getUserOrganizations(realm, user)).willReturn(Stream.of(org));
        given(org.getPosition(user)).willReturn(position);
        ExternalUserProfile profile = profileProvider.create(formData, user);

        profile.update();

        then(organizationProvider).should().getUserOrganizations(realm, user);
    }

    @Test
    void should_create_new_org_for_resident_head() {
        ExternalAttributes expected = validAppendedLegalResidentHead();
        MultivaluedMap<String, String> formData = originalForm(expected);
        given(keycloakSession.getContext()).willReturn(context);
        given(context.getRealm()).willReturn(realm);
        given(keycloakSession.getProvider(OrganizationProvider.class)).willReturn(organizationProvider);
        given(organizationProvider.getOrganizationByBin(realm, expected.bin())).willReturn(null);
        given(organizationProvider.createOrganization(realm, user)).willReturn(org);
        given(org.getPosition(user)).willReturn(position);
        given(position.getName()).willReturn(expected.legalRole().toUpperCase());
        ExternalUserProfile profile = profileProvider.create(formData, user);

        profile.update();

        then(organizationProvider).should().createOrganization(realm, user);
    }

    @Test
    void should_request_position_for_non_resident_employee() {
        ExternalAttributes expected = validLegalNonResidentEmployee();
        MultivaluedMap<String, String> formData = originalForm(expected);
        given(keycloakSession.getContext()).willReturn(context);
        given(context.getRealm()).willReturn(realm);
        given(keycloakSession.getProvider(OrganizationProvider.class)).willReturn(organizationProvider);
        given(organizationProvider.getOrganizationByBin(realm, expected.bin())).willReturn(org);
        given(org.getPosition(user)).willReturn(null);
        ExternalUserProfile profile = profileProvider.create(formData, user);

        profile.update();

        then(org).should().requestPosition(expected.legalRole().toUpperCase(), user);
    }

    @Test
    void should_request_position_for_resident_employee() {
        ExternalAttributes expected = validAppendedLegalResidentEmployee();
        MultivaluedMap<String, String> formData = originalForm(expected);
        given(keycloakSession.getContext()).willReturn(context);
        given(context.getRealm()).willReturn(realm);
        given(keycloakSession.getProvider(OrganizationProvider.class)).willReturn(organizationProvider);
        given(organizationProvider.getOrganizationByBin(realm, expected.bin())).willReturn(org);
        given(org.getPosition(user)).willReturn(null);
        ExternalUserProfile profile = profileProvider.create(formData, user);

        profile.update();

        then(org).should().requestPosition(expected.legalRole().toUpperCase(), user);
    }

    @Test
    void should_create_new_head_in_existing_resident_legal() {
        ExternalAttributes expected = validAppendedLegalResidentHead();
        MultivaluedMap<String, String> formData = originalForm(expected);
        given(keycloakSession.getContext()).willReturn(context);
        given(context.getRealm()).willReturn(realm);
        given(keycloakSession.getProvider(OrganizationProvider.class)).willReturn(organizationProvider);
        given(organizationProvider.getOrganizationByBin(realm, expected.bin())).willReturn(org);
        given(org.getPositions()).willReturn(Stream.empty());
        given(org.getPosition(user)).willReturn(null);
        given(org.requestPosition(expected.legalRole().toUpperCase(), user)).willReturn(position);
        ExternalUserProfile profile = profileProvider.create(formData, user);

        profile.update();

        then(org).should().confirmPosition(position);
    }

    @Test
    void should_remove_old_head_when_new_head_is_creating_in_resident_legal() {
        ExternalAttributes expected = validAppendedLegalResidentHead();
        MultivaluedMap<String, String> formData = originalForm(expected);
        UserModel oldHeadUser = mock(UserModel.class);
        given(oldHeadUser.getId()).willReturn(UUID.randomUUID().toString());
        PositionModel oldHead = mock(PositionModel.class);
        given(oldHead.getUser()).willReturn(oldHeadUser);
        given(oldHead.getName()).willReturn(PositionModel.HEAD);
        given(keycloakSession.getContext()).willReturn(context);
        given(context.getRealm()).willReturn(realm);
        given(keycloakSession.getProvider(OrganizationProvider.class)).willReturn(organizationProvider);
        given(organizationProvider.getOrganizationByBin(realm, expected.bin())).willReturn(org);
        given(org.getPositions()).willReturn(Stream.of(oldHead));
        given(org.getPosition(user)).willReturn(null);
        given(org.requestPosition(expected.legalRole().toUpperCase(), user)).willReturn(position);
        given(org.requestPosition(PositionModel.EMPLOYEE, oldHeadUser)).willReturn(null);
        ExternalUserProfile profile = profileProvider.create(formData, user);

        profile.update();

        then(org).should().removePosition(oldHead);
        then(org).should().requestPosition(PositionModel.EMPLOYEE, oldHeadUser);
    }

    @Test
    void should_make_fake_head_employee_in_resident_legal() {
        ExternalAttributes expected = validAppendedLegalResidentEmployee();
        MultivaluedMap<String, String> formData = originalForm(expected);
        PositionModel oldHead = mock(PositionModel.class);
        given(oldHead.getName()).willReturn(PositionModel.HEAD);
        given(keycloakSession.getContext()).willReturn(context);
        given(context.getRealm()).willReturn(realm);
        given(keycloakSession.getProvider(OrganizationProvider.class)).willReturn(organizationProvider);
        given(organizationProvider.getOrganizationByBin(realm, expected.bin())).willReturn(org);
        given(org.getPosition(user)).willReturn(oldHead);
        given(org.requestPosition(expected.legalRole().toUpperCase(), user)).willReturn(position);
        ExternalUserProfile profile = profileProvider.create(formData, user);

        profile.update();

        then(org).should().removePosition(oldHead);
        then(org).should().requestPosition(PositionModel.EMPLOYEE, user);
    }

    @Test
    void should_make_fake_employee_head_in_resident_legal() {
        ExternalAttributes expected = validAppendedLegalResidentHead();
        MultivaluedMap<String, String> formData = originalForm(expected);
        PositionModel oldEmployee = mock(PositionModel.class);
        given(oldEmployee.getName()).willReturn(PositionModel.EMPLOYEE);
        given(keycloakSession.getContext()).willReturn(context);
        given(context.getRealm()).willReturn(realm);
        given(keycloakSession.getProvider(OrganizationProvider.class)).willReturn(organizationProvider);
        given(organizationProvider.getOrganizationByBin(realm, expected.bin())).willReturn(org);
        given(org.getPositions()).willReturn(Stream.empty());
        given(org.getPosition(user)).willReturn(oldEmployee);
        given(org.requestPosition(expected.legalRole().toUpperCase(), user)).willReturn(position);
        ExternalUserProfile profile = profileProvider.create(formData, user);

        profile.update();

        then(org).should().removePosition(oldEmployee);
        then(org).should().confirmPosition(position);
    }

    @Test
    void should_swap_head_in_resident_legal() {
        ExternalAttributes expected = validAppendedLegalResidentHead();
        MultivaluedMap<String, String> formData = originalForm(expected);
        UserModel oldHeadUser = mock(UserModel.class);
        given(oldHeadUser.getId()).willReturn(UUID.randomUUID().toString());
        PositionModel oldHead = mock(PositionModel.class);
        given(oldHead.getUser()).willReturn(oldHeadUser);
        given(oldHead.getName()).willReturn(PositionModel.HEAD);
        PositionModel oldEmployee = mock(PositionModel.class);
        given(oldEmployee.getName()).willReturn(PositionModel.EMPLOYEE);
        given(keycloakSession.getContext()).willReturn(context);
        given(context.getRealm()).willReturn(realm);
        given(keycloakSession.getProvider(OrganizationProvider.class)).willReturn(organizationProvider);
        given(organizationProvider.getOrganizationByBin(realm, expected.bin())).willReturn(org);
        given(org.getPositions()).willReturn(Stream.of(oldHead, oldEmployee));
        given(org.getPosition(user)).willReturn(oldEmployee);
        given(org.requestPosition(expected.legalRole().toUpperCase(), user)).willReturn(position);
        given(org.requestPosition(PositionModel.EMPLOYEE, oldHeadUser)).willReturn(null);
        ExternalUserProfile profile = profileProvider.create(formData, user);

        profile.update();

        then(org).should().removePosition(oldHead);
        then(org).should().removePosition(oldEmployee);
        then(org).should().requestPosition(PositionModel.EMPLOYEE, oldHeadUser);
        then(org).should().confirmPosition(position);
    }
}
