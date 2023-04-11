package kz.kacd.sso.external.migration.account.kafka.model;

import kz.kacd.sso.external.migration.account.kafka.representation.DrscbPersonRepresentation;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class DrscbAccountFactoryTest {

    @Test
    void should_convert_id_to_string() {
        BigDecimal expected = BigDecimal.valueOf(123456789000000L);
        DrscbPersonRepresentation source = new DrscbPersonRepresentation();
        source.setId(expected);
        source.setIrs("2");

        DrscbAccount actual = DrscbAccountFactory.create(source);

        assertThat(actual.getId()).isEqualTo(expected.toString());
    }

    @Test
    void should_correctly_identify_personal_account() {
        DrscbPersonRepresentation source = new DrscbPersonRepresentation();
        source.setId(BigDecimal.ONE);
        source.setIrs("1");
        source.setIin("123456789012");

        DrscbAccount actual = DrscbAccountFactory.create(source);

        assertThat(actual.getKind()).isEqualTo(DrscbAccount.Kind.PERSONAL_ACCOUNT);
    }

    @Test
    void should_correctly_identify_legal_account() {
        DrscbPersonRepresentation source = new DrscbPersonRepresentation();
        source.setId(BigDecimal.ONE);
        source.setIrs("1");
        source.setBin("123456789012");

        DrscbAccount actual = DrscbAccountFactory.create(source);

        assertThat(actual.getKind()).isEqualTo(DrscbAccount.Kind.LEGAL_ACCOUNT);
    }

    @Test
    void should_correctly_identify_employee_account() {
        DrscbPersonRepresentation source = new DrscbPersonRepresentation();
        source.setId(BigDecimal.ONE);
        source.setIrs("1");
        source.setIin("123456789012");
        source.setBin("123456789012");

        DrscbAccount actual = DrscbAccountFactory.create(source);

        assertThat(actual.getKind()).isEqualTo(DrscbAccount.Kind.LEGAL_EMPLOYEE_ACCOUNT);
    }

    @Test
    void should_identify_resident() {
        DrscbPersonRepresentation source = new DrscbPersonRepresentation();
        source.setId(BigDecimal.ONE);
        source.setIrs("1");

        DrscbAccount actual = DrscbAccountFactory.create(source);

        assertThat(actual.isResident()).isTrue();
    }

    @Test
    void should_identify_nonresident() {
        DrscbPersonRepresentation source = new DrscbPersonRepresentation();
        source.setId(BigDecimal.ONE);
        source.setIrs("2");

        DrscbAccount actual = DrscbAccountFactory.create(source);

        assertThat(actual.isResident()).isFalse();
    }

    @Test
    void should_format_phone_number() {
        DrscbPersonRepresentation source = new DrscbPersonRepresentation();
        source.setId(BigDecimal.ONE);
        source.setIrs("2");
        source.setmPhone("+7-777-777-77-77");

        DrscbAccount actual = DrscbAccountFactory.create(source);

        assertThat(actual.getPhoneNumber()).isEqualTo("+77777777777");
    }
}