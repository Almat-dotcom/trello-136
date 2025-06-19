package kz.kacd.sso.external.flow.login;

import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;

import kz.kacd.sso.external.model.page.ExternalLoginPage;
import kz.kacd.sso.external.model.page.ExternalRegistrationPage;

import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.FlowStatus;
import org.keycloak.authentication.authenticators.browser.AbstractUsernameFormAuthenticator;
import org.keycloak.authentication.authenticators.browser.UsernamePasswordForm;
import org.keycloak.models.UserModel;
import org.keycloak.services.managers.AuthenticationManager;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static kz.kacd.sso.external.flow.login.utils.LoginInfoUtils.*;

/**
 * Расширенный Username-Password форм-аутентификатор под Keycloak 23.
 *
 * <ul>
 *   <li>Если пользователь вводит 12-значный ИИН, класс автоматически
 *       превращает его в «&lt;iin&gt;-physical» до того, как Keycloak
 *       начнёт искать пользователя в БД.</li>
 *   <li>После успешного логина суффикс убирается, чтобы в события и логи
 *       попал «чистый» ИИН.</li>
 *   <li>Поддерживает цепочку альтернативных аутентификаторов и валидаторов,
 *       как в оригинальной реализации.</li>
 * </ul>
 */
public class ExtendedUsernamePasswordForm extends UsernamePasswordForm {

    private static final Logger LOG = Logger.getLogger(ExtendedUsernamePasswordForm.class);

    private final List<AlternativeAuthenticator> alternatives;
    private final List<AuthenticatorValidator>   validators;

    public ExtendedUsernamePasswordForm(List<AlternativeAuthenticator> alternatives,
                                        List<AuthenticatorValidator>   validators) {
        this.alternatives = addDefaultAuthenticatorAtTheEndOfTheFlow(alternatives);
        this.validators   = validators;
    }

    /* ────────────────────────────────────────────────────────────────────
       1. Добавляем «дефолтный» Alternative, который:
          ▸ подменяет имя на «iin-physical» перед super.action()
          ▸ вызывает супер-логику UsernamePasswordForm
          ▸ откатывает имя обратно после удачного логина
       ────────────────────────────────────────────────────────────────── */
    private List<AlternativeAuthenticator> addDefaultAuthenticatorAtTheEndOfTheFlow(
            List<AlternativeAuthenticator> source) {

        List<AlternativeAuthenticator> list = new ArrayList<>(source);
        list.add(new AlternativeAuthenticator() {

            @Override public boolean isConfiguredFor(AuthenticationFlowContext ctx) {
                return true;
            }

            @Override public void action(AuthenticationFlowContext ctx) {
                String rawUsername = extractUsername(ctx);
                boolean isIin      = isIin(rawUsername);

                /* 1) До супер-обработки подменяем username  */
                if (isIin) rewriteContextUsername(ctx, rawUsername);

                /* 2) Запускаем стандартный UsernamePasswordForm.action()    */
                ExtendedUsernamePasswordForm.super.action(ctx);

                /* 3) Если логин прошёл OK, откатываем имя и пишем деталь    */
                if (ctx.getStatus() == FlowStatus.SUCCESS) {
                    ctx.getEvent().detail(ExternalLoginPage.AUTHENTICATION_TYPE, "password");
                    if (isIin) rewriteContextIin(ctx, rawUsername);
                }
            }
        });

        return list;
    }

    /* ────────────────────────────────────────────────────────────────────
                                   ACTION (POST)
       ────────────────────────────────────────────────────────────────── */
    @Override
    public void action(AuthenticationFlowContext ctx) {

        LOG.debug("ExtendedUsernamePasswordForm.action() start");

        /* 1. Прогоняем альтернативы; последняя — наша дефолтная. */
        processAuthenticators(ctx);

        /* 2. Если супер-логика поставила SUCCESS, валидируем и логируем. */
        if (ctx.getStatus() == FlowStatus.SUCCESS) {
            processValidation(ctx);
        } else {
            ctx.getEvent().detail("error", ctx.getUserErrorMessage());
        }
    }

    /* ────────────────────────────────────────────────────────────────────
                                   HELPERS
       ────────────────────────────────────────────────────────────────── */
    private String extractUsername(AuthenticationFlowContext ctx) {
        return ctx.getHttpRequest()
                .getDecodedFormParameters()
                .getFirst(AuthenticationManager.FORM_USERNAME);
    }

    private boolean isIin(String username) {
        return username != null && username.length() == 12 && username.matches("\\d+");
    }

    /** Подменяем имя на «iin-physical» и обновляем ATTEMPTED_USERNAME. */
    private void rewriteContextUsername(AuthenticationFlowContext ctx, String iin) {
        setUsername(ctx, iin + "-" + ExternalRegistrationPage.CLIENT_PHYSICAL);
    }

    /** Возвращаем изначальный ИИН после успешного прохождения flow. */
    private void rewriteContextIin(AuthenticationFlowContext ctx, String iin) {
        setUsername(ctx, iin);
    }

    private void setUsername(AuthenticationFlowContext ctx, String username) {
        MultivaluedMap<String,String> form =
                ctx.getHttpRequest().getDecodedFormParameters();

        form.putSingle(AuthenticationManager.FORM_USERNAME, username);

        /* В KC 23 поиск пользователя идёт по ATTEMPTED_USERNAME */
        ctx.getAuthenticationSession()
                .setAuthNote(AbstractUsernameFormAuthenticator.ATTEMPTED_USERNAME, username);

        LOG.infof("Patched username → %s", username);
    }

    /* ────────────────────────────────────────────────────────────────────
                           ALTERNATIVES / VALIDATORS
       ────────────────────────────────────────────────────────────────── */
    private void processAuthenticators(AuthenticationFlowContext ctx) {
        for (AlternativeAuthenticator alt : alternatives) {
            if (alt.isConfiguredFor(ctx)) {
                alt.action(ctx);
                break;
            }
        }
    }

    private void processValidation(AuthenticationFlowContext ctx) {
        LOG.debug("Authentication succeeded. Running validators ...");

        UserModel user = ctx.getUser();
        if (user == null) {
            throw new IllegalStateException("Authenticator chain did not set user!");
        }

        AuthenticatorValidator.Error error = null;
        for (AuthenticatorValidator v : validators) {
            error = v.validate(user, ctx.getSession(), ctx);
            if (error != null) break;
        }

        if (error != null) {
            boolean clearUser = !isUserAlreadySetBeforeUsernamePasswordAuth(ctx);
            failAuthentication(ctx, error, clearUser);
            return;
        }

        storeLastLogin(ctx, user);
        ctx.success();
    }

    private void failAuthentication(AuthenticationFlowContext ctx,
                                    AuthenticatorValidator.Error error,
                                    boolean clearUser) {
        if (clearUser) ctx.clearUser();
        Response challenge = challenge(ctx, error.getMessage(), error.getField());
        ctx.challenge(challenge);
    }

    /* ────────────────────────────────────────────────────────────────────
                         ЛОГИРОВАНИЕ ПОСЛЕДНЕГО ЛОГИНА
       ────────────────────────────────────────────────────────────────── */
    private void storeLastLogin(AuthenticationFlowContext ctx, UserModel user) {
        copyPreviousLoginAttributes(user);

        String time      = getCurrentFormattedTime();
        String ip        = getClientIP(ctx);
        String userAgent = ctx.getHttpRequest().getHttpHeaders().getHeaderString(HEADER_USER_AGENT);

        String browser = detectBrowser(userAgent);
        String os      = detectOS(userAgent);

        user.setSingleAttribute(LAST_LOGIN_TIME, time);
        user.setSingleAttribute(LAST_LOGIN_IP,   ip);
        user.setSingleAttribute(LAST_LOGIN_OS,   os);
        user.setSingleAttribute(LAST_LOGIN_BROWSER, browser);

        LOG.infof("Stored login info for '%s': time=%s, IP=%s, os=%s, browser=%s",
                user.getUsername(), time, ip, os, browser);
    }

    private void copyPreviousLoginAttributes(UserModel user) {
        copyAttribute(user, LAST_LOGIN_TIME,    PREVIOUS_LOGIN_TIME);
        copyAttribute(user, LAST_LOGIN_IP,      PREVIOUS_LOGIN_IP);
        copyAttribute(user, LAST_LOGIN_OS,      PREVIOUS_LOGIN_OS);
        copyAttribute(user, LAST_LOGIN_BROWSER, PREVIOUS_LOGIN_BROWSER);
    }

    private void copyAttribute(UserModel user, String oldAttr, String newAttr) {
        String value = user.getFirstAttribute(oldAttr);
        if (Objects.nonNull(value)) user.setSingleAttribute(newAttr, value);
    }

    private String getCurrentFormattedTime() {
        return DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)
                .withZone(TIME_ZONE_OFFSET)
                .format(Instant.now());
    }

    private String getClientIP(AuthenticationFlowContext ctx) {
        String forwarded = ctx.getHttpRequest().getHttpHeaders().getHeaderString(HEADER_X_FORWARDED_FOR);
        return (forwarded != null && !forwarded.isEmpty())
                ? forwarded.split(",")[0].trim()
                : ctx.getSession().getContext().getConnection().getRemoteAddr();
    }

    private String detectBrowser(String ua) {
        if (ua.contains("Edg"))                                                      return "Edge";
        if (ua.contains("Chrome")   && ua.contains("Safari")   && !ua.contains("Edg")) return "Chrome";
        if (ua.contains("Firefox")  && !ua.contains("Chrome"))                        return "Firefox";
        if (ua.contains("Safari")   && !ua.contains("Chrome")   && !ua.contains("Edg")) return "Safari";
        if (ua.contains("OPR")      || ua.contains("Opera"))                          return "Opera";
        if (ua.contains("Brave"))                                                    return "Brave";
        if (ua.contains("Trident"))                                                  return "Internet Explorer";
        return UNKNOWN_BROWSER;
    }

    private String detectOS(String ua) {
        if (ua.contains("Windows NT 10.0")) return "Windows 10";
        if (ua.contains("Windows NT 6.3"))  return "Windows 8.1";
        if (ua.contains("Windows NT 6.2"))  return "Windows 8";
        if (ua.contains("Windows NT 6.1"))  return "Windows 7";
        if (ua.contains("iPhone") || ua.contains("iPad") || ua.contains("iPod")) return "iOS";
        if (ua.contains("Mac OS X"))        return "Mac OS X";
        if (ua.contains("Android"))         return "Android";
        if (ua.contains("Linux"))           return "Linux";
        return UNKNOWN_OS;
    }
}