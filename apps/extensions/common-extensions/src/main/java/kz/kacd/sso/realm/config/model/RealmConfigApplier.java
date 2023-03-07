package kz.kacd.sso.realm.config.model;

import kz.kacd.sso.v1.RealmSpec;
import kz.kacd.sso.v1.realmspec.*;
import kz.kacd.sso.v1.realmspec.events.AdminEvents;
import kz.kacd.sso.v1.realmspec.events.UserEvents;
import kz.kacd.sso.v1.realmspec.login.Email;
import kz.kacd.sso.v1.realmspec.login.LoginScreen;
import kz.kacd.sso.v1.realmspec.security.BruteForce;
import kz.kacd.sso.v1.realmspec.security.Headers;
import kz.kacd.sso.v1.realmspec.sessions.Client;
import kz.kacd.sso.v1.realmspec.sessions.Offline;
import kz.kacd.sso.v1.realmspec.sessions.Sso;
import kz.kacd.sso.v1.realmspec.themes.Localization;
import org.keycloak.common.enums.SslRequired;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;

import java.util.Collections;
import java.util.HashSet;
import java.util.stream.Collectors;

import static kz.kacd.sso.util.ValueUtils.defaulted;
import static kz.kacd.sso.util.ValueUtils.duration;

public class RealmConfigApplier {

    private static final String ADMIN_EVENTS_EXPIRATION = "adminEventsExpiration";
    private static final String FRONT_END_URL = "frontendUrl";
    private static final String DEFAULT_THEME = "keycloak";

    private final RealmModel model;
    private final KeycloakSession session;

    public RealmConfigApplier(RealmModel model, KeycloakSession session) {
        this.model = model;
        this.session = session;
    }

    public RealmModel apply(String name, RealmSpec source) {
        model.setEnabled(true);
        applyNameAndSsl(name, source);
        applyLogin(source.getLogin());
        applyEmail(source.getEmail());
        applyThemes(source.getThemes());
        applyEvents(source.getEvents());
        applySecurity(source.getSecurity());
        applySession(source.getSessions());
        applyTokens(source.getTokens());
        applyPasswordPolicy(source.getAuthentication());

        return model;
    }

    private void applyNameAndSsl(String name, RealmSpec source) {
        model.setDisplayName(defaulted(source.getDisplayedName(), name));
        model.setDisplayNameHtml(model.getDisplayName());
        model.setSslRequired(convertSsl(defaulted(source.getRequireSsl(), "none")));
        if (source.getFrontendUrl() != null) {
            model.setAttribute(FRONT_END_URL, source.getFrontendUrl());
        } else {
            model.removeAttribute(FRONT_END_URL);
        }
    }

    private SslRequired convertSsl(String source) {
        switch (source) {
            case "all":
                return SslRequired.ALL;
            case "external":
                return SslRequired.EXTERNAL;
            default:
                return SslRequired.NONE;
        }
    }

    private void applyLogin(Login source) {
        Login spec = source;
        if (spec == null) {
            spec = new Login();
        }

        applyLoginScreen(spec.getLoginScreen());
        applyLoginEmail(spec.getEmail());
    }

    private void applyLoginScreen(LoginScreen source) {
        LoginScreen spec = source;
        if (spec == null) {
            spec = new LoginScreen();
        }

        model.setRegistrationAllowed(defaulted(spec.getRegistration(), false));
        model.setResetPasswordAllowed(defaulted(spec.getForgotPassword(), false));
        model.setRememberMe(defaulted(spec.getRememberMe(), false));
    }

    private void applyLoginEmail(Email source) {
        Email spec = source;
        if (spec == null) {
            spec = new Email();
        }

        model.setRegistrationEmailAsUsername(defaulted(spec.getEmailAsUsername(), false));
        model.setLoginWithEmailAllowed(defaulted(spec.getLoginWithEmail(), true));
        model.setDuplicateEmailsAllowed(defaulted(spec.getDuplicatesEmails(), false));
        model.setVerifyEmail(defaulted(spec.getVerifyEmail(), false));
    }

    private void applyEmail(kz.kacd.sso.v1.realmspec.Email source) {
        if (source == null) {
            return;
        }

        model.setSmtpConfig(new SMTPServerBuilder().withFrom(source.getFrom(), source.getDisplayedName()).withHostAndPort(source.getSmtpHost(), source.getSmtpPort().intValue()).withSsl(source.getEncryption()).withAuth(source.getAuthentication(), session).build());
    }

    private void applyThemes(Themes source) {
        Themes spec = source;
        if (spec == null) {
            spec = new Themes();
        }

        model.setLoginTheme(defaulted(spec.getLogin(), DEFAULT_THEME));
        model.setAccountTheme(defaulted(spec.getAccount(), DEFAULT_THEME));
        model.setAdminTheme(defaulted(spec.getAdmin(), DEFAULT_THEME));
        model.setEmailTheme(defaulted(spec.getEmail(), DEFAULT_THEME));
        applyLocalization(spec.getLocalization());
    }

    private void applyLocalization(Localization source) {
        if (source == null) {
            return;
        }

        model.setInternationalizationEnabled(defaulted(source.getI18n(), false));
        if (model.isInternationalizationEnabled()) {
            model.setSupportedLocales(new HashSet<>(defaulted(source.getSupportedLocales().stream().map(it -> it.name().toLowerCase()).collect(Collectors.toList()), Collections.emptyList())));
            model.setDefaultLocale(defaulted(source.getDefaultLocale().name().toLowerCase(), "en"));
        }
    }

    private void applyEvents(Events source) {
        if (source == null) {
            return;
        }

        model.setEventsListeners(new HashSet<>(defaulted(source.getEventListeners(), Collections.emptyList())));
        applyUserEvents(source.getUserEvents());
        applyAdminEvents(source.getAdminEvents());
    }

    private void applyUserEvents(UserEvents source) {
        if (source == null) {
            return;
        }

        model.setEventsEnabled(defaulted(source.getSaveEvents(), false));
        if (model.isEventsEnabled()) {
            model.setEventsExpiration(duration(defaulted(source.getExpiration(), "7d")));
            model.setEnabledEventTypes(new HashSet<>(defaulted(source.getSavedTypes(), Collections.emptyList())));
        }
    }

    private void applyAdminEvents(AdminEvents source) {
        if (source == null) {
            return;
        }

        model.setAdminEventsEnabled(defaulted(source.getSaveEvents(), false));
        if (model.isAdminEventsEnabled()) {
            model.setAdminEventsDetailsEnabled(defaulted(source.getIncludeRepresentation(), false));
            model.setAttribute(ADMIN_EVENTS_EXPIRATION, duration(defaulted(source.getExpiration(), "7d")));
        }
    }

    private void applySecurity(Security source) {
        if (source == null) {
            return;
        }

        applyContentPolicy(source.getHeaders());
        applyBruteForce(source.getBruteForce());
    }

    private void applyContentPolicy(Headers source) {
        if (source == null) {
            return;
        }

        new ContentPolicyBuilder().buildFrom(source).forEach(model::setAttribute);
    }

    private void applyBruteForce(BruteForce source) {
        if (source == null) {
            return;
        }

        new BruteForceConfigurer(source).configure(model);
    }

    private void applySession(Sessions source) {
        Sessions spec = source;
        if (spec == null) {
            spec = new Sessions();
            spec.setClient(new Client());
            spec.setOffline(new Offline());
            spec.setSso(new Sso());
        }

        new SessionsConfigurer(spec).configure(model);
    }

    private void applyTokens(Tokens source) {
        Tokens spec = source;
        if (spec == null) {
            spec = new Tokens();
        }

        model.setAccessTokenLifespan(duration(defaulted(spec.getAccessLifespan(), "15m")).intValue());
        model.setAccessTokenLifespanForImplicitFlow(duration(defaulted(spec.getOidcAccessLifespan(), "15m")).intValue());
    }

    private void applyPasswordPolicy(Authentication spec) {
        if (spec == null || spec.getPasswordPolicy() == null) {
            return;
        }

        model.setPasswordPolicy(new PasswordPolicyBuilder(session, model).build(spec.getPasswordPolicy()));
    }
}
