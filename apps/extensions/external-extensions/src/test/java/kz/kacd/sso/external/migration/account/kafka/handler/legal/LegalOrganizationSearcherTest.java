package kz.kacd.sso.external.migration.account.kafka.handler.legal;

import kz.kacd.sso.external.migration.account.kafka.model.DrscbAccount;
import kz.kacd.sso.external.migration.account.kafka.model.DrscbAccountFactory;
import kz.kacd.sso.external.migration.account.kafka.model.DrscbAttributes;
import kz.kacd.sso.external.migration.account.kafka.representation.DrscbPersonRepresentation;
import kz.kacd.sso.external.model.OrganizationModel;
import kz.kacd.sso.external.model.OrganizationProvider;
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
class LegalOrganizationSearcherTest {

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

    LegalOrganizationSearcher searcher;

    @BeforeEach
    void setUp() {
        given(session.getContext()).willReturn(context);
        given(context.getRealm()).willReturn(realm);
        searcher = new LegalOrganizationSearcher(session);
    }

    @Test
    void should_find_legal_resident_by_idn() {
        String bin = "123456789012";
        DrscbAccount arg = legalResident(bin);
        OrganizationModel expected = mock(OrganizationModel.class);
        given(session.getProvider(OrganizationProvider.class)).willReturn(orgs);
        given(orgs.getOrganizationByBin(realm, bin)).willReturn(expected);

        OrganizationModel actual = searcher.find(arg);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void should_find_legal_nonresident_by_drscb_idn() {
        String drscbIdn = "000000000123";
        DrscbAccount arg = legalNonResident(drscbIdn);
        UserModel employee = mock(UserModel.class);
        given(session.users()).willReturn(users);
        given(users.searchForUserByUserAttributeStream(realm, DrscbAttributes.LEGAL_NON_RESIDENT_IDN, drscbIdn))
                .willReturn(Stream.of(employee));
        OrganizationModel expected = mock(OrganizationModel.class);
        given(session.getProvider(OrganizationProvider.class)).willReturn(orgs);
        given(orgs.getUserOrganizations(realm, employee))
                .willReturn(Stream.of(expected));

        OrganizationModel actual = searcher.find(arg);

        assertThat(actual).isEqualTo(expected);
    }

    private DrscbAccount legalResident(String bin) {
        DrscbPersonRepresentation repr = new DrscbPersonRepresentation();
        repr.setId(BigDecimal.ONE);
        repr.setBin(bin);
        repr.setIrs("1");
        return DrscbAccountFactory.create(repr);
    }

    private DrscbAccount legalNonResident(String bin) {
        DrscbPersonRepresentation repr = new DrscbPersonRepresentation();
        repr.setId(BigDecimal.ONE);
        repr.setBin(bin);
        repr.setIrs("2");
        return DrscbAccountFactory.create(repr);
    }

}