package kz.kacd.sso.external.flow.login;

import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.models.UserModel;
import org.keycloak.models.credential.OTPCredentialModel;
import org.keycloak.credential.CredentialProvider;
import org.keycloak.credential.OTPCredentialProvider;
import org.keycloak.authentication.authenticators.browser.OTPFormAuthenticator;

public class ConditionalOTPAuthenticator extends OTPFormAuthenticator {

    private static final Logger logger = Logger.getLogger(ConditionalOTPAuthenticator.class);

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        UserModel user = context.getUser();

        // Проверяем, есть ли пользователь в контексте
        if (user == null) {
            logger.warn("No user found in the authentication context.");
            context.failure(AuthenticationFlowError.UNKNOWN_USER);
            return;
        }

        // Проверяем, настроен ли у пользователя OTP
        if (!isOTPConfigured(context, user)) {
            logger.infof("User %s does not have OTP configured. Skipping OTP step.", user.getUsername());
            context.success(); // Пропускаем шаг OTP
            return;
        }

        logger.infof("User %s has OTP configured. Prompting for OTP code.", user.getUsername());

        // Показываем форму для ввода OTP
        Response challenge = context.form()
                .setAttribute("realm", context.getRealm())
                .createForm("login-otp.ftl");
        context.challenge(challenge);
    }

    @Override
    public void action(AuthenticationFlowContext context) {
        super.action(context); // Используем стандартную валидацию OTP из OTPFormAuthenticator
    }

    private boolean isOTPConfigured(AuthenticationFlowContext context, UserModel user) {
        // Получаем провайдер OTP
        OTPCredentialProvider otpProvider = (OTPCredentialProvider) context.getSession()
                .getProvider(CredentialProvider.class, OTPCredentialModel.TYPE);

        if (otpProvider == null) {
            logger.warn("OTP Credential Provider is not available.");
            return false;
        }

        // Проверяем, настроен ли OTP для пользователя
        return otpProvider.isConfiguredFor(context.getRealm(), user);
    }
}
