package kz.kacd.sso.external.flow.login;

import jakarta.ws.rs.core.Response;
import kz.kacd.sso.external.model.page.ExternalLoginPage;
import kz.kacd.sso.external.model.page.ExternalRegistrationPage;
import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.FlowStatus;
import org.keycloak.authentication.authenticators.browser.UsernamePasswordForm;
import org.keycloak.models.UserModel;
import org.keycloak.services.managers.AuthenticationManager;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static kz.kacd.sso.external.flow.login.utils.LoginInfoUtils.*;

/**
 * Extends standard username password form.
 * </>
 * Fill your alternatives list which will be used before standard username password validation.
 * If your alternative returns true, all following alternatives will be ignored.
 * </>
 * Fill your validators list to provide additional validations after successful login.
 */
public class ExtendedUsernamePasswordForm extends UsernamePasswordForm implements Authenticator {
    private static final Logger log = Logger.getLogger(ExtendedUsernamePasswordForm.class);

    private final List<AlternativeAuthenticator> alternatives;
    private final List<AuthenticatorValidator> validators;

    public ExtendedUsernamePasswordForm(
            List<AlternativeAuthenticator> alternatives,
            List<AuthenticatorValidator> validators
    ) {
        this.alternatives = addDefaultAuthenticatorAtTheEndOfTheFlow(alternatives);
        this.validators = validators;
    }

    private List<AlternativeAuthenticator> addDefaultAuthenticatorAtTheEndOfTheFlow(
            List<AlternativeAuthenticator> source
    ) {
        List<AlternativeAuthenticator> extendedAlternatives = new ArrayList<>(source);
        extendedAlternatives.add(new AlternativeAuthenticator() {
            @Override
            public boolean isConfiguredFor(AuthenticationFlowContext context) {
                return true;
            }

            @Override
            public void action(AuthenticationFlowContext context) {
                log.info("I am here");
                String username = extractUsername(context);
                boolean isIin = isIin(username);
                if (isIin) {
                    String login = username + "-" + ExternalRegistrationPage.CLIENT_PHYSICAL;

                    log.infof("[IIN-FORM] normalized login = %s", login);
                    UserModel user = context.getSession()
                            .users()
                            .getUserByUsername(context.getRealm(), login);

                    if (user == null) {
                        context.failure(AuthenticationFlowError.INVALID_USER);
                        return;
                    }

                    /* назначаем пользователя и фиксируем notes */
                    context.setUser(user);
                    log.infof("[IIN-FORM] setUser → %s (id=%s)", user.getUsername(), user.getId());
                    context.getAuthenticationSession()
                            .setAuthNote("ATTEMPTED_USERNAME", login);
                    context.getAuthenticationSession().setAuthNote("USER_SET_BEFORE_USERNAME_PASSWORD_AUTH", "true");
                    context.getAuthenticationSession()
                            .setAuthNote(AuthenticationManager.FORM_USERNAME,login);
                }
                ExtendedUsernamePasswordForm.super.action(context);
                if (FlowStatus.SUCCESS.equals(context.getStatus())) {
                    context.getEvent().detail(ExternalLoginPage.AUTHENTICATION_TYPE, "password");
                    log.info("[IIN-FORM] login SUCCESS");
                }
            }
        });
        return extendedAlternatives;
    }

    private String extractUsername(AuthenticationFlowContext context) {
        return context.getHttpRequest().getDecodedFormParameters().getFirst(AuthenticationManager.FORM_USERNAME);
    }

    private boolean isIin(String username) {
        return username.length() == 12 && username.matches("\\d+");
    }

    private void rewriteContextUsername(AuthenticationFlowContext context, String iin) {
        String username = iin + "-" + ExternalRegistrationPage.CLIENT_PHYSICAL;
        setUsername(context, username);
    }


    private void rewriteContextIin(AuthenticationFlowContext context, String iin) {
        setUsername(context, iin);
    }

    private void setUsername(AuthenticationFlowContext context, String username) {
        log.infof("Almat setUsername(%s)", username);
        context.getHttpRequest().getDecodedFormParameters().putSingle(AuthenticationManager.FORM_USERNAME, username);
    }

    @Override
    public void action(AuthenticationFlowContext context) {
        log.debug("Executing user form processing ...");

        processAuthenticators(context);

        if (context.getStatus().equals(FlowStatus.SUCCESS)) {
            processValidation(context);
        }

        if (!context.getStatus().equals(FlowStatus.SUCCESS)) {
            context.getEvent().detail("error", context.getUserErrorMessage());
        }
    }

    private void processAuthenticators(AuthenticationFlowContext context) {
        log.debug("Processing authenticators ...");
        for (AlternativeAuthenticator auth : alternatives) {
            boolean executed = processAuth(auth, context);
            if (executed) {
                break;
            }
        }
    }

    private boolean processAuth(AlternativeAuthenticator authenticator, AuthenticationFlowContext context) {
        if (authenticator.isConfiguredFor(context)) {
            log.infof(
                    "Authenticator %s is configured to process current context. Executing it ...",
                    authenticator.getClass().getSimpleName()
            );
            authenticator.action(context);
            return true;
        }
        return false;
    }

    private void storeLastLogin(AuthenticationFlowContext context, UserModel user) {
        copyPreviousLoginAttributes(user);

        String time = getCurrentFormattedTime();
        String ip = getClientIP(context);
        String userAgent = context.getHttpRequest().getHttpHeaders().getHeaderString(HEADER_USER_AGENT);

        String browser = detectBrowser(userAgent);
        String os = detectOS(userAgent);

        user.setSingleAttribute(LAST_LOGIN_TIME, time);
        user.setSingleAttribute(LAST_LOGIN_IP, ip);
        user.setSingleAttribute(LAST_LOGIN_OS, os);
        user.setSingleAttribute(LAST_LOGIN_BROWSER, browser);

        log.infof("Stored login info for user '%s': time=%s, IP=%s, os=%s, browser=%s",
                user.getUsername(), time, ip, os, browser);
    }

    private void copyPreviousLoginAttributes(UserModel user) {
        copyAttribute(user, LAST_LOGIN_TIME, PREVIOUS_LOGIN_TIME);
        copyAttribute(user, LAST_LOGIN_IP, PREVIOUS_LOGIN_IP);
        copyAttribute(user, LAST_LOGIN_OS, PREVIOUS_LOGIN_OS);
        copyAttribute(user, LAST_LOGIN_BROWSER, PREVIOUS_LOGIN_BROWSER);
    }

    private void copyAttribute(UserModel user, String oldAttr, String newAttr) {
        String value = user.getFirstAttribute(oldAttr);
        if (Objects.nonNull(value)) {
            user.setSingleAttribute(newAttr, value);
        }
    }

    private String getCurrentFormattedTime() {
        return DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)
                .withZone(TIME_ZONE_OFFSET)
                .format(Instant.now());
    }

    private String getClientIP(AuthenticationFlowContext context) {
        String forwarded = context.getHttpRequest().getHttpHeaders().getHeaderString(HEADER_X_FORWARDED_FOR);
        return (forwarded != null && !forwarded.isEmpty())
                ? forwarded.split(",")[0].trim()
                : context.getSession().getContext().getConnection().getRemoteAddr();
    }

    private String detectBrowser(String userAgent) {
        if (userAgent.contains("Edg")) return "Edge";
        if (userAgent.contains("Chrome") && userAgent.contains("Safari") && !userAgent.contains("Edg")) return "Chrome";
        if (userAgent.contains("Firefox") && !userAgent.contains("Chrome")) return "Firefox";
        if (userAgent.contains("Safari") && !userAgent.contains("Chrome") && !userAgent.contains("Edg")) return "Safari";
        if (userAgent.contains("OPR") || userAgent.contains("Opera")) return "Opera";
        if (userAgent.contains("Brave")) return "Brave";
        if (userAgent.contains("Trident")) return "Internet Explorer";
        return UNKNOWN_BROWSER;
    }



    private String detectOS(String userAgent) {
        if (userAgent.contains("Windows NT 10.0")) return "Windows 10";
        if (userAgent.contains("Windows NT 6.3")) return "Windows 8.1";
        if (userAgent.contains("Windows NT 6.2")) return "Windows 8";
        if (userAgent.contains("Windows NT 6.1")) return "Windows 7";
        if (userAgent.contains("iPhone") || userAgent.contains("iPad") || userAgent.contains("iPod")) return "iOS";
        if (userAgent.contains("Mac OS X")) return "Mac OS X";
        if (userAgent.contains("Android")) return "Android";
        if (userAgent.contains("Linux")) return "Linux";
        return UNKNOWN_OS;
    }

    private void processValidation(AuthenticationFlowContext context) {
        log.debug("Authentication succeeded. Validating authentication ...");
        UserModel user = context.getUser();
        if (user == null) {
            throw new IllegalStateException("Authenticators does not add user to authentication context!");
        }

        AuthenticatorValidator.Error error = null;
        for (AuthenticatorValidator validator : validators) {
            error = validator.validate(user, context.getSession(), context);
            if (error != null) {
                break;
            }
        }

        if (error != null) {
            boolean clearUser = !isUserAlreadySetBeforeUsernamePasswordAuth(context);
            failAuthentication(context, error, clearUser);
            return;
        }

        storeLastLogin(context, user);

        context.success();
    }

    private void failAuthentication(AuthenticationFlowContext context, AuthenticatorValidator.Error error, boolean clearUser) {
        log.debugf("Validation failed with %s in field %s ...", error.getMessage(), error.getField());
        if (clearUser) {
            context.clearUser();
        }
        Response challenge = challenge(context, error.getMessage(), error.getField());
        context.challenge(challenge);
    }
}