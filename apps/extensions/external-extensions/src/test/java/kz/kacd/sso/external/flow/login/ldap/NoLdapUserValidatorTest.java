package kz.kacd.sso.external.flow.login.ldap;

import kz.kacd.sso.external.flow.login.AuthenticatorValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.UserModel;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class NoLdapUserValidatorTest {

    @Mock
    UserModel user;
    @Mock
    KeycloakSession keycloakSession;
    @Mock
    AuthenticationFlowContext context;

    NoLdapUserValidator validator = new NoLdapUserValidator();

    @Test
    void should_deny_ldap_user() {
        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put("division", Collections.singletonList("ORIS"));
        given(user.getAttributes()).willReturn(attributes);

        AuthenticatorValidator.Error error = validator.validate(user, keycloakSession, context);

        assertThat(error).isNotNull();
    }

    @Test
    void should_allow_non_ldap_user() {
        Map<String, List<String>> attributes = new HashMap<>();
        given(user.getAttributes()).willReturn(attributes);

        AuthenticatorValidator.Error error = validator.validate(user, keycloakSession, context);

        assertThat(error).isNull();
    }
}