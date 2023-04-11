package kz.kacd.sso.external.migration.account.kafka.handler;

import kz.kacd.sso.external.migration.account.kafka.model.DrscbAccountContext;
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
class UserSearcherTest {

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

    UserSearcher searcher;

    @BeforeEach
    void setUp() {
        given(session.getContext()).willReturn(context);
        given(context.getRealm()).willReturn(realm);
        searcher = new UserSearcher(session);
    }

    @Test
    void should_find_physical_resident_by_iin() {
        String iin = "123456789012";
        DrscbAccountContext arg = physicalResident(iin);
        UserModel expected = mock(UserModel.class);
        given(expected.getFirstAttribute(ExternalRegistrationPage.FIELD_CLIENT_TYPE))
                .willReturn(ExternalRegistrationPage.CLIENT_PHYSICAL);
        given(session.users()).willReturn(users);
        given(users.searchForUserByUserAttributeStream(realm, "iin", iin))
                .willReturn(Stream.of(expected));

        searcher.handle(arg);

        assertThat(arg.getUser()).isEqualTo(expected);
    }

    @Test
    void should_find_physical_nonresident_by_email() {
        String email = "yop@some.com";
        DrscbAccountContext arg = physicalNonResident(email);
        UserModel expected = mock(UserModel.class);
        given(expected.getFirstAttribute(ExternalRegistrationPage.FIELD_CLIENT_TYPE))
                .willReturn(ExternalRegistrationPage.CLIENT_PHYSICAL);
        given(session.users()).willReturn(users);
        given(users.getUserByEmail(realm, email)).willReturn(expected);

        searcher.handle(arg);

        assertThat(arg.getUser()).isEqualTo(expected);
    }

    @Test
    void should_find_legal_resident_by_idn() {
        String bin = "123456789012";
        DrscbAccountContext arg = legalResident(bin);
        OrganizationModel expected = mock(OrganizationModel.class);
        given(session.getProvider(OrganizationProvider.class)).willReturn(orgs);
        given(orgs.getOrganizationByBin(realm, bin)).willReturn(expected);

        searcher.handle(arg);

        assertThat(arg.getOrganization()).isEqualTo(expected);
    }

    @Test
    void should_find_legal_nonresident_by_drscb_idn() {
        String drscbIdn = "000000000123";
        DrscbAccountContext arg = legalNonResident(drscbIdn);
        UserModel employee = mock(UserModel.class);
        given(session.users()).willReturn(users);
        given(users.searchForUserByUserAttributeStream(realm, DrscbAttributes.LEGAL_NON_RESIDENT_IDN, drscbIdn))
                .willReturn(Stream.of(employee));
        OrganizationModel expected = mock(OrganizationModel.class);
        given(session.getProvider(OrganizationProvider.class)).willReturn(orgs);
        given(orgs.getUserOrganizations(realm, employee))
                .willReturn(Stream.of(expected));

        searcher.handle(arg);

        assertThat(arg.getOrganization()).isEqualTo(expected);
    }

    @Test
    void should_find_employee_resident_by_bin_and_iin() {
        String bin = "123456789012";
        String iin = "111111111111";
        DrscbAccountContext arg = employeeResident(bin, iin);
        OrganizationModel organization = mock(OrganizationModel.class);
        given(session.getProvider(OrganizationProvider.class)).willReturn(orgs);
        given(orgs.getOrganizationByBin(realm, bin)).willReturn(organization);
        PositionModel position = mock(PositionModel.class);
        given(organization.getPositions()).willReturn(Stream.of(position));
        UserModel expected = mock(UserModel.class);
        given(position.getUser()).willReturn(expected);
        given(expected.getUsername())
                .willReturn(iin + "-" + ExternalRegistrationPage.CLIENT_LEGAL);

        searcher.handle(arg);

        assertThat(arg.getUser()).isEqualTo(expected);
        assertThat(arg.getPosition()).isEqualTo(position);
        assertThat(arg.getOrganization()).isEqualTo(organization);
    }

    @Test
    void should_find_employee_nonresindent() {
        String drscbIdn = "000000000123";
        String email = "some@some.com";
        DrscbAccountContext arg = employeeNonResident(drscbIdn, email);
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

        searcher.handle(arg);

        assertThat(arg.getUser()).isEqualTo(expected);
        assertThat(arg.getPosition()).isEqualTo(position);
        assertThat(arg.getOrganization()).isEqualTo(organization);
    }

    private DrscbAccountContext physicalResident(String iin) {
        DrscbPersonRepresentation repr = new DrscbPersonRepresentation();
        repr.setId(BigDecimal.ONE);
        repr.setIin(iin);
        repr.setIrs("1");
        return new DrscbAccountContext(DrscbAccountFactory.create(repr));
    }

    private DrscbAccountContext physicalNonResident(String email) {
        DrscbPersonRepresentation repr = new DrscbPersonRepresentation();
        repr.setId(BigDecimal.ONE);
        repr.setEmail(email);
        repr.setIrs("2");
        return new DrscbAccountContext(DrscbAccountFactory.create(repr));
    }

    private DrscbAccountContext legalResident(String bin) {
        DrscbPersonRepresentation repr = new DrscbPersonRepresentation();
        repr.setId(BigDecimal.ONE);
        repr.setBin(bin);
        repr.setIrs("1");
        return new DrscbAccountContext(DrscbAccountFactory.create(repr));
    }

    private DrscbAccountContext legalNonResident(String bin) {
        DrscbPersonRepresentation repr = new DrscbPersonRepresentation();
        repr.setId(BigDecimal.ONE);
        repr.setBin(bin);
        repr.setIrs("2");
        return new DrscbAccountContext(DrscbAccountFactory.create(repr));
    }

    private DrscbAccountContext employeeResident(String bin, String iin) {
        DrscbPersonRepresentation repr = new DrscbPersonRepresentation();
        repr.setId(BigDecimal.ONE);
        repr.setBin(bin);
        repr.setIin(iin);
        repr.setIrs("1");
        return new DrscbAccountContext(DrscbAccountFactory.create(repr));
    }

    private DrscbAccountContext employeeNonResident(String bin, String email) {
        DrscbPersonRepresentation repr = new DrscbPersonRepresentation();
        repr.setId(BigDecimal.ONE);
        repr.setBin(bin);
        repr.setIin("0000000000000000");
        repr.setEmail(email);
        repr.setIrs("2");
        return new DrscbAccountContext(DrscbAccountFactory.create(repr));
    }
}