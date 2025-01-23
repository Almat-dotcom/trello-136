package kz.kacd.sso.external.flow.login;

import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.authenticators.browser.OTPFormAuthenticator;

/**
 * Сам класс, который расширяет стандартный OTPFormAuthenticator.
 * Вы можете переопределить методы authenticate() / action(), чтобы внести изменения.
 */
public class ExtendedOTPFormAuthenticator extends OTPFormAuthenticator {

    private static final Logger LOG = Logger.getLogger(ExtendedOTPFormAuthenticator.class);

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        LOG.info("ExtendedOTPFormAuthenticator: authenticate() called");
        // Вызываем логику родителя, чтобы не ломать поведение
        super.authenticate(context);
    }

    @Override
    public void action(AuthenticationFlowContext context) {
        LOG.info("ExtendedOTPFormAuthenticator: action() called");
        // Опять же делегируем родительской логике
        super.action(context);
    }

    // При необходимости переопределите и другие методы
}
