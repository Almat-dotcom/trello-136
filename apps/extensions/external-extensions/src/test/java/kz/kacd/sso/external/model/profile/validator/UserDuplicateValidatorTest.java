package kz.kacd.sso.external.model.profile.validator;

import kz.kacd.sso.external.model.profile.ExternalAttributes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.models.*;
import org.keycloak.validate.ValidationError;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.Consumer;

import static kz.kacd.sso.external.model.profile.TestProfileFactory.validPhysicalResident;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDuplicateValidatorTest {

    @Mock
    Consumer<ValidationError> listener;
    @Mock
    KeycloakSession keycloakSession;
    @Mock
    UserProvider userProvider;
    @Mock
    RealmModel realm;
    @Mock
    KeycloakContext context;
    @InjectMocks
    UserDuplicateValidator validator;

    @Test
    void should_accept_not_duplicate_user() {
        ExternalAttributes attributes = validPhysicalResident();
        given(keycloakSession.users()).willReturn(userProvider);
        given(keycloakSession.getContext()).willReturn(context);
        given(context.getRealm()).willReturn(realm);
        given(userProvider.getUserByUsername(realm, attributes.username())).willReturn(null);
        given(userProvider.getUserByEmail(realm, attributes.email())).willReturn(null);

        validator.validate(attributes);

        then(listener).should(never()).accept(any());
    }

    @Test
    void should_not_accept_duplicate_username() {
        ExternalAttributes attributes = validPhysicalResident();
        given(keycloakSession.users()).willReturn(userProvider);
        given(keycloakSession.getContext()).willReturn(context);
        given(context.getRealm()).willReturn(realm);
        given(userProvider.getUserByUsername(realm, attributes.username())).willReturn(mock(UserModel.class));
        given(userProvider.getUserByEmail(realm, attributes.email())).willReturn(null);

        validator.validate(attributes);

        then(listener).should(times(1)).accept(any());
    }

    @Test
    void should_not_accept_duplicate_email() {
        ExternalAttributes attributes = validPhysicalResident();
        given(keycloakSession.users()).willReturn(userProvider);
        given(keycloakSession.getContext()).willReturn(context);
        given(context.getRealm()).willReturn(realm);
        given(userProvider.getUserByUsername(realm, attributes.username())).willReturn(null);
        given(userProvider.getUserByEmail(realm, attributes.email())).willReturn(mock(UserModel.class));

        validator.validate(attributes);

        then(listener).should(times(1)).accept(any());
    }
}