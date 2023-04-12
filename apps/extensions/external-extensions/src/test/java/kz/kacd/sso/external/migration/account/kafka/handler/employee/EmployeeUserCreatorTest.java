package kz.kacd.sso.external.migration.account.kafka.handler.employee;

import kz.kacd.sso.external.migration.account.kafka.model.DrscbAccount;
import kz.kacd.sso.external.migration.account.kafka.model.DrscbAccountFactory;
import kz.kacd.sso.external.migration.account.kafka.representation.DrscbPersonRepresentation;
import kz.kacd.sso.external.model.OrganizationModel;
import kz.kacd.sso.external.model.OrganizationProvider;
import kz.kacd.sso.external.model.PositionModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.models.*;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static kz.kacd.sso.external.model.util.ExternalModelUtils.externalLegalResidentUsername;
import static kz.kacd.sso.external.model.util.ExternalModelUtils.externalNonResidentUsername;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class EmployeeUserCreatorTest {

    @Mock
    KeycloakSession session;
    @Mock
    KeycloakContext context;
    @Mock
    RealmModel realm;
    @Mock
    OrganizationProvider orgs;
    @Mock
    UserProvider users;

    EmployeeUserCreator creator;

    @BeforeEach
    void setUp() {
        given(session.getContext()).willReturn(context);
        given(context.getRealm()).willReturn(realm);
        creator = new EmployeeUserCreator(session);
    }

    @Test
    void should_create_resident_employee() {
        String bin = "123456789012";
        String iin = "123456789012";
        DrscbAccount account = employeeResident(bin, iin);
        given(session.getProvider(OrganizationProvider.class)).willReturn(orgs);
        OrganizationModel organization = mock(OrganizationModel.class);
        given(orgs.getOrganizationByBin(realm, bin)).willReturn(organization);
        given(session.users()).willReturn(users);
        UserModel expected = mock(UserModel.class);
        given(users.addUser(realm, externalLegalResidentUsername(iin)))
                .willReturn(expected);

        UserModel actual = creator.create(account);

        assertThat(actual).isEqualTo(expected);
        then(organization).should().requestPosition(PositionModel.EMPLOYEE, expected);
    }

    @Test
    void should_create_nonresident_employee() {
        String drscbIdn = "123456789012";
        String email = "some@some.com";
        DrscbAccount account = employeeNonResident(drscbIdn, email);
        given(session.getProvider(OrganizationProvider.class)).willReturn(orgs);
        OrganizationModel organization = mock(OrganizationModel.class);
        given(orgs.getOrganizationByBin(realm, drscbIdn)).willReturn(organization);
        given(session.users()).willReturn(users);
        UserModel expected = mock(UserModel.class);
        given(users.addUser(realm, externalNonResidentUsername(email)))
                .willReturn(expected);

        UserModel actual = creator.create(account);

        assertThat(actual).isEqualTo(expected);
        then(organization).should().requestPosition(PositionModel.EMPLOYEE, expected);
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
        repr.setEmail(email);
        repr.setIrs("2");
        return DrscbAccountFactory.create(repr);
    }
}