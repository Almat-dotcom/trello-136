package kz.kacd.sso.external.flow.login;

import jakarta.ws.rs.core.Response;
import kz.kacd.sso.external.model.page.ExternalLoginPage;
import kz.kacd.sso.external.model.page.ExternalRegistrationPage;
import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.FlowStatus;
import org.keycloak.authentication.authenticators.browser.UsernamePasswordForm;
import org.keycloak.models.UserModel;
import org.keycloak.services.managers.AuthenticationManager;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

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
                String username = extractUsername(context);
                boolean isIin = isIin(username);
                if (isIin) {
                    rewriteContextUsername(context, username);
                }

                ExtendedUsernamePasswordForm.super.action(context);

                if (context.getStatus().equals(FlowStatus.SUCCESS)) {
                    context.getEvent().detail(ExternalLoginPage.AUTHENTICATION_TYPE, "password");
                }

                if (isIin) {
                    rewriteContextIin(context, username);
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
            log.debugf(
                    "Authenticator %s is configured to process current context. Executing it ...",
                    authenticator.getClass().getSimpleName()
            );
            authenticator.action(context);
            return true;
        }
        return false;
    }

    private void storeLastLogin(AuthenticationFlowContext context, UserModel user) {
        ZoneId almatyZone = ZoneId.of("Asia/Almaty");

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(almatyZone);
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm").withZone(almatyZone);

        Instant now = Instant.now();
        String formattedDate = dateFormatter.format(now);
        String formattedTime = timeFormatter.format(now);

        String ip = context.getSession().getContext().getConnection().getRemoteAddr();

        user.setSingleAttribute("date", formattedDate);
        user.setSingleAttribute("time", formattedTime);
        user.setSingleAttribute("ip", ip);

        log.infof("Stored login info for user '%s': date=%s, time=%s, IP=%s",
                user.getUsername(), formattedDate, formattedTime, ip);
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
