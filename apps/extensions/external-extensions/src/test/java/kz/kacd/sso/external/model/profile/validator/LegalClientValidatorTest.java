package kz.kacd.sso.external.model.profile.validator;

import kz.kacd.sso.external.model.OrganizationModel;
import kz.kacd.sso.external.model.OrganizationProvider;
import kz.kacd.sso.external.model.profile.ExternalAttributes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.models.KeycloakContext;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.validate.ValidationError;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.Consumer;

import static kz.kacd.sso.external.model.profile.TestProfileFactory.validAppendedLegalResidentEmployee;
import static kz.kacd.sso.external.model.profile.TestProfileFactory.validAppendedLegalResidentHead;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LegalClientValidatorTest {

    @Mock
    Consumer<ValidationError> listener;
    @Mock
    KeycloakSession keycloakSession;
    @Mock
    KeycloakContext context;
    @Mock
    RealmModel realm;
    @Mock
    OrganizationProvider organizationProvider;
    @InjectMocks
    LegalClientValidator validator;

    @Test
    void should_accept_new_organization_creation_by_head() {
        ExternalAttributes attributes = validAppendedLegalResidentHead();
        given(keycloakSession.getContext()).willReturn(context);
        given(context.getRealm()).willReturn(realm);
        given(keycloakSession.getProvider(OrganizationProvider.class)).willReturn(organizationProvider);
        given(organizationProvider.getOrganizationByBin(realm, attributes.bin())).willReturn(null);

        validator.validate(attributes);

        then(listener).should(never()).accept(any());
    }

    @Test
    void should_accept_head_registration_if_org_resident_exists() {
        ExternalAttributes attributes = validAppendedLegalResidentHead();
        given(keycloakSession.getContext()).willReturn(context);
        given(context.getRealm()).willReturn(realm);
        given(keycloakSession.getProvider(OrganizationProvider.class)).willReturn(organizationProvider);
        given(organizationProvider.getOrganizationByBin(realm, attributes.bin())).willReturn(mock(OrganizationModel.class));

        validator.validate(attributes);

        then(listener).should(never()).accept(any());
    }

    @Test
    void should_accept_employee_registration_if_org_exists() {
        ExternalAttributes attributes = validAppendedLegalResidentEmployee();
        given(keycloakSession.getContext()).willReturn(context);
        given(context.getRealm()).willReturn(realm);
        given(keycloakSession.getProvider(OrganizationProvider.class)).willReturn(organizationProvider);
        given(organizationProvider.getOrganizationByBin(realm, attributes.bin())).willReturn(mock(OrganizationModel.class));

        validator.validate(attributes);

        then(listener).should(never()).accept(any());
    }

    @Test
    void should_not_accept_new_organization_creation_by_employee() {
        ExternalAttributes attributes = validAppendedLegalResidentEmployee();
        given(keycloakSession.getContext()).willReturn(context);
        given(context.getRealm()).willReturn(realm);
        given(keycloakSession.getProvider(OrganizationProvider.class)).willReturn(organizationProvider);
        given(organizationProvider.getOrganizationByBin(realm, attributes.bin())).willReturn(null);

        validator.validate(attributes);

        then(listener).should(times(1)).accept(any());
    }
}