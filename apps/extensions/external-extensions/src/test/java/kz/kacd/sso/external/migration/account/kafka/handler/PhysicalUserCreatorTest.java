package kz.kacd.sso.external.migration.account.kafka.handler;

import kz.kacd.sso.external.migration.account.kafka.handler.physical.PhysicalUserCreator;
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
import java.net.URLEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class PhysicalUserCreatorTest {

    @Mock
    KeycloakSession session;
    @Mock
    KeycloakContext context;
    @Mock
    RealmModel realm;
    @Mock
    UserProvider users;

    PhysicalUserCreator creator;

    @BeforeEach
    void setUp() {
        given(session.getContext()).willReturn(context);
        given(context.getRealm()).willReturn(realm);
        creator = new PhysicalUserCreator(session);
    }

    @Test
    void should_create_new_resident() {
        String iin = "1234567890";
        DrscbAccount arg = resident(iin);
        given(session.users()).willReturn(users);
        UserModel expected = mock(UserModel.class);
        given(users.addUser(realm, iin + "-" + ExternalRegistrationPage.CLIENT_PHYSICAL))
                .willReturn(expected);

        UserModel actual = creator.create(arg);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void should_create_new_non_resident() {
        String email = "some@some.com";
        DrscbAccount arg = nonResident(email);
        given(session.users()).willReturn(users);
        UserModel expected = mock(UserModel.class);
        given(users.addUser(realm, URLEncoder.encode(email)))
                .willReturn(expected);

        UserModel actual = creator.create(arg);

        assertThat(actual).isEqualTo(expected);
    }

    private DrscbAccount resident(String iin) {
        DrscbPersonRepresentation repr = new DrscbPersonRepresentation();
        repr.setId(BigDecimal.ONE);
        repr.setIin(iin);
        repr.setIrs("1");
        return DrscbAccountFactory.create(repr);
    }

    private DrscbAccount nonResident(String email) {
        DrscbPersonRepresentation repr = new DrscbPersonRepresentation();
        repr.setId(BigDecimal.ONE);
        repr.setEmail(email);
        repr.setIrs("2");
        return DrscbAccountFactory.create(repr);
    }

}