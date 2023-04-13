package kz.kacd.sso.external.migration.account.kafka.handler.employee;

import kz.kacd.sso.external.migration.account.kafka.handler.legal.LegalOrganizationSearcher;
import kz.kacd.sso.external.migration.account.kafka.model.DrscbAccount;
import kz.kacd.sso.external.migration.account.kafka.model.DrscbAccountFactory;
import kz.kacd.sso.external.migration.account.kafka.model.DrscbAttributes;
import kz.kacd.sso.external.migration.account.kafka.representation.DrscbPersonRepresentation;
import kz.kacd.sso.external.model.OrganizationModel;
import kz.kacd.sso.external.model.OrganizationProvider;
import kz.kacd.sso.external.model.PositionModel;
import kz.kacd.sso.external.model.page.ExternalRegistrationPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.models.*;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class EmployeeUserSearcherTest {

    @Mock
    KeycloakSession session;
    @Mock
    KeycloakContext context;
    @Mock
    OrganizationProvider orgs;
    @Mock
    UserProvider users;
    @Mock
    RealmModel realm;

    EmployeeUserSearcher searcher;

    @BeforeEach
    void setUp() {
        given(session.getContext()).willReturn(context);
        given(context.getRealm()).willReturn(realm);
        searcher = new EmployeeUserSearcher(new LegalOrganizationSearcher(session));
    }

    @Test
    void should_find_employee_resident_by_bin_and_iin() {
        String bin = "123456789012";
        String iin = "111111111111";
        DrscbAccount arg = employeeResident(bin, iin);
        OrganizationModel organization = mock(OrganizationModel.class);
        given(session.getProvider(OrganizationProvider.class)).willReturn(orgs);
        given(orgs.getOrganizationByBin(realm, bin)).willReturn(organization);
        PositionModel position = mock(PositionModel.class);
        given(organization.getPositions()).willReturn(Stream.of(position));
        UserModel expected = mock(UserModel.class);
        given(position.getUser()).willReturn(expected);
        given(expected.getUsername())
                .willReturn(iin + "-" + ExternalRegistrationPage.CLIENT_LEGAL);

        UserModel actual = searcher.find(arg);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void should_find_employee_nonresindent() {
        String drscbIdn = "000000000123";
        String email = "some@some.com";
        DrscbAccount arg = employeeNonResident(drscbIdn, email);
        UserModel expected = mock(UserModel.class);
        given(session.users()).willReturn(users);
        given(users.searchForUserByUserAttributeStream(realm, DrscbAttributes.LEGAL_NON_RESIDENT_IDN, drscbIdn))
                .willReturn(Stream.of(expected));
        OrganizationModel organization = mock(OrganizationModel.class);
        given(session.getProvider(OrganizationProvider.class)).willReturn(orgs);
        given(orgs.getUserOrganizations(realm, expected))
                .willReturn(Stream.of(organization));
        PositionModel position = mock(PositionModel.class);
        given(organization.getPositions()).willReturn(Stream.of(position));
        given(position.getUser()).willReturn(expected);
        given(expected.getEmail()).willReturn(email);

        UserModel actual = searcher.find(arg);

        assertThat(actual).isEqualTo(expected);
    }

    private DrscbAccount employeeResident(String bin, String iin) {
        DrscbPersonRepresentation repr = new DrscbPersonRepresentation();
        repr.setId(BigDecimal.ONE);
        repr.setBin(bin);
        repr.setIin(iin);
        repr.setIrs("1");
        return DrscbAccountFactory.create(repr);
    }

    private DrscbAccount employeeNonResident(String bin, String email) {
        DrscbPersonRepresentation repr = new DrscbPersonRepresentation();
        repr.setId(BigDecimal.ONE);
        repr.setBin(bin);
        repr.setIin("0000000000000000");
        repr.setEmail(email);
        repr.setIrs("2");
        return DrscbAccountFactory.create(repr);
    }

}