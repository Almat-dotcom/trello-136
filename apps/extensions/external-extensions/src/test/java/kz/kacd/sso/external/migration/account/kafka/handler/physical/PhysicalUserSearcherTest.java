package kz.kacd.sso.external.migration.account.kafka.handler.physical;

import kz.kacd.sso.external.migration.account.kafka.model.DrscbAccount;
import kz.kacd.sso.external.migration.account.kafka.model.DrscbAccountFactory;
import kz.kacd.sso.external.migration.account.kafka.representation.DrscbPersonRepresentation;
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
class PhysicalUserSearcherTest {

    @Mock
    KeycloakSession session;
    @Mock
    KeycloakContext context;
    @Mock
    UserProvider users;
    @Mock
    RealmModel realm;

    PhysicalUserSearcher searcher;

    @BeforeEach
    void setUp() {
        given(session.getContext()).willReturn(context);
        given(context.getRealm()).willReturn(realm);
        searcher = new PhysicalUserSearcher(session);
    }

    @Test
    void should_find_physical_resident_by_iin() {
        String iin = "123456789012";
        DrscbAccount arg = physicalResident(iin);
        UserModel expected = mock(UserModel.class);
        given(expected.getFirstAttribute(ExternalRegistrationPage.FIELD_CLIENT_TYPE))
                .willReturn(ExternalRegistrationPage.CLIENT_PHYSICAL);
        given(session.users()).willReturn(users);
        given(users.searchForUserByUserAttributeStream(realm, "iin", iin))
                .willReturn(Stream.of(expected));

        UserModel actual = searcher.find(arg);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void should_find_physical_nonresident_by_email() {
        String email = "yop@kldas.com";
        DrscbAccount arg = physicalNonResident(email);
        UserModel expected = mock(UserModel.class);
        given(expected.getFirstAttribute(ExternalRegistrationPage.FIELD_CLIENT_TYPE))
                .willReturn(ExternalRegistrationPage.CLIENT_PHYSICAL);
        given(session.users()).willReturn(users);
        given(users.getUserByEmail(realm, email)).willReturn(expected);

        UserModel actual = searcher.find(arg);

        assertThat(actual).isEqualTo(expected);
    }

    private DrscbAccount physicalResident(String iin) {
        DrscbPersonRepresentation repr = new DrscbPersonRepresentation();
        repr.setId(BigDecimal.ONE);
        repr.setIin(iin);
        repr.setIrs("1");
        return DrscbAccountFactory.create(repr);
    }

    private DrscbAccount physicalNonResident(String email) {
        DrscbPersonRepresentation repr = new DrscbPersonRepresentation();
        repr.setId(BigDecimal.ONE);
        repr.setEmail(email);
        repr.setIrs("2");
        return DrscbAccountFactory.create(repr);
    }
}