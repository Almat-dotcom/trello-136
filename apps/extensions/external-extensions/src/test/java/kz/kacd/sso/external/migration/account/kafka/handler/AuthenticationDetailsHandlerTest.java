package kz.kacd.sso.external.migration.account.kafka.handler;

import kz.kacd.sso.external.migration.account.kafka.model.DrscbAccount;
import kz.kacd.sso.external.migration.account.kafka.model.DrscbAccountFactory;
import kz.kacd.sso.external.migration.account.kafka.representation.DrscbPersonRepresentation;
import kz.kacd.sso.external.requiredaction.email.ChangeEmailRequiredAction;
import kz.kacd.sso.external.requiredaction.phone.ChangePhoneRequiredAction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.models.UserModel;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
class AuthenticationDetailsHandlerTest {

    @Mock
    UserModel user;

    AuthenticationDetailsHandler handler = new AuthenticationDetailsHandler(session);

    @Test
    void should_apply_new_email_and_add_verify_email_action() {
        String email = "my@email.com";
        DrscbAccount account = account(email, null);
        ArgumentCaptor<String> emailCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<UserModel.RequiredAction> actionCaptor = ArgumentCaptor.forClass(UserModel.RequiredAction.class);
        doNothing().when(user).setEmail(emailCaptor.capture());
        doNothing().when(user).addRequiredAction(actionCaptor.capture());

        handler.applyAuthenticationDetails(account, user);

        assertThat(emailCaptor.getValue()).isEqualTo(email);
        assertThat(actionCaptor.getValue()).isEqualTo(UserModel.RequiredAction.VERIFY_EMAIL);
    }

    @Test
    void should_force_user_to_change_email() {
        String fake = "123@" + DrscbAccount.MOCK_EMAIL;
        DrscbAccount account = account(fake, "+77777777777");
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        doNothing().when(user).addRequiredAction(captor.capture());

        handler.applyAuthenticationDetails(account, user);

        assertThat(captor.getValue()).isEqualTo(ChangeEmailRequiredAction.PROVIDER_ID);
    }

    @Test
    void should_force_user_to_change_phone_number() {
        String email = "test@t.me";
        DrscbAccount account = account(email, null);
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        doNothing().when(user).addRequiredAction(any(UserModel.RequiredAction.class));
        doNothing().when(user).addRequiredAction(captor.capture());

        handler.applyAuthenticationDetails(account, user);

        assertThat(captor.getValue()).isEqualTo(ChangePhoneRequiredAction.PROVIDER_ID);
    }

    private DrscbAccount account(String email, String phone) {
        DrscbPersonRepresentation representation = new DrscbPersonRepresentation();
        representation.setEmail(email);
        representation.setmPhone(phone);
        representation.setId(BigDecimal.ONE);
        representation.setIrs("2");
        return DrscbAccountFactory.create(representation);
    }
}