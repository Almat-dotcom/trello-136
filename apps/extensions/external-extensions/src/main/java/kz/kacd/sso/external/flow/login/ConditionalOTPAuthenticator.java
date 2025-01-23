package kz.kacd.sso.external.flow.login;

import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.jboss.logging.Logger;

/**
 * Если у пользователя включён OTP (например, через атрибут "otp_enabled"),
 * тогда переходим к следующему шагу (где стоит OTPForm).
 * Иначе пропускаем (context.success()).
 */
public class ConditionalOTPAuthenticator implements Authenticator {

    private static final Logger LOG = Logger.getLogger(ConditionalOTPAuthenticator.class);

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        UserModel user = context.getUser();
        boolean otpEnabled = UserAttributeUtils.isOtpEnabled(user);

        if (otpEnabled) {
            LOG.infof("User [%s] has OTP enabled, going to OTP form", user.getUsername());
            // Даем понять, что шаг "выполнен, но нужна проверка дальше"
            // Обычно ставят context.attempted(), чтобы переходить к следующему Execution
            context.attempted();
        } else {
            LOG.infof("User [%s] has no OTP enabled, skipping OTP check", user.getUsername());
            // Завершаем аутентификацию без OTP
            context.success();
        }
    }

    @Override
    public void action(AuthenticationFlowContext context) {
        // Здесь ничего не делаем, т.к. форма у нас не показывается напрямую из этого шага
    }

    @Override
    public boolean requiresUser() {
        return true;
    }

    @Override
    public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
        // Считаем, что настроек не требуется, шаг всегда "готов"
        return true;
    }

    @Override
    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {
        // Ничего не требуем
    }

    @Override
    public void close() {
        // Пусто
    }
}
