package kz.kacd.sso.external.model.profile.validator;

import kz.kacd.sso.external.model.page.ExternalRegistrationPage;
import kz.kacd.sso.external.model.profile.ExternalAttributes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.validate.ValidationError;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.function.Consumer;

import static kz.kacd.sso.external.model.profile.TestProfileFactory.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class FieldConsistencyValidatorTest {

    @Mock
    Consumer<ValidationError> listener;
    @InjectMocks
    FieldConsistencyValidator validator;

    @Test
    void should_not_accept_resident_physical_without_iin() {
        ExternalAttributes attributes = validPhysicalResident();
        attributes.add(Collections.singletonMap(ExternalRegistrationPage.FIELD_IIN, ""));

        validator.validate(attributes);

        then(listener).should(times(1)).accept(any());
    }

    @Test
    void should_not_accept_resident_legal_without_iin() {
        ExternalAttributes attributes = validAppendedLegalResidentHead();
        attributes.add(Collections.singletonMap(ExternalRegistrationPage.FIELD_IIN, ""));

        validator.validate(attributes);

        then(listener).should(times(1)).accept(any());
    }

    @Test
    void should_not_accept_resident_legal_without_bin() {
        ExternalAttributes attributes = validAppendedLegalResidentHead();
        attributes.add(Collections.singletonMap(ExternalRegistrationPage.FIELD_BIN, ""));

        validator.validate(attributes);

        then(listener).should(times(1)).accept(any());
    }

    @Test
    void should_not_accept_non_resident_legal_employee_without_bin() {
        ExternalAttributes attributes = validLegalNonResidentEmployee();
        attributes.add(Collections.singletonMap(ExternalRegistrationPage.FIELD_BIN, ""));

        validator.validate(attributes);

        then(listener).should(times(1)).accept(any());
    }

    @Test
    void should_accept_valid_physical_resident() {
        ExternalAttributes attributes = validPhysicalResident();

        validator.validate(attributes);

        then(listener).should(never()).accept(any());
    }

    @Test
    void should_accept_valid_physical_non_resident() {
        ExternalAttributes attributes = validPhysicalNonResident();

        validator.validate(attributes);

        then(listener).should(never()).accept(any());
    }

    @Test
    void should_accept_valid_legal_resident_head() {
        ExternalAttributes attributes = validAppendedLegalResidentHead();

        validator.validate(attributes);

        then(listener).should(never()).accept(any());
    }

    @Test
    void should_accept_valid_legal_resident_employee() {
        ExternalAttributes attributes = validAppendedLegalResidentEmployee();

        validator.validate(attributes);

        then(listener).should(never()).accept(any());
    }

    @Test
    void should_accept_valid_legal_non_resident_head() {
        ExternalAttributes attributes = validLegalNonResidentHead();

        validator.validate(attributes);

        then(listener).should(never()).accept(any());
    }

    @Test
    void should_accept_valid_legal_non_resident_employee() {
        ExternalAttributes attributes = validLegalNonResidentEmployee();

        validator.validate(attributes);

        then(listener).should(never()).accept(any());
    }
}