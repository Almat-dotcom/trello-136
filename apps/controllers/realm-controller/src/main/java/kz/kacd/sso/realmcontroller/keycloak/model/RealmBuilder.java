package kz.kacd.sso.realmcontroller.keycloak.model;

import kz.kacd.sso.realmcontroller.k8s.crd.model.RealmSpec;
import kz.kacd.sso.realmcontroller.k8s.crd.model.email.EmailSpec;
import kz.kacd.sso.realmcontroller.k8s.crd.model.event.AdminEventsSpec;
import kz.kacd.sso.realmcontroller.k8s.crd.model.event.EventsSpec;
import kz.kacd.sso.realmcontroller.k8s.crd.model.event.UserEventsSpec;
import kz.kacd.sso.realmcontroller.k8s.crd.model.localization.LocalizationSpec;
import kz.kacd.sso.realmcontroller.k8s.crd.model.login.LoginEmailSpec;
import kz.kacd.sso.realmcontroller.k8s.crd.model.login.LoginScreenSpec;
import kz.kacd.sso.realmcontroller.k8s.crd.model.login.LoginSpec;
import kz.kacd.sso.realmcontroller.k8s.crd.model.login.LoginUserInfoSpec;
import kz.kacd.sso.realmcontroller.k8s.crd.model.security.SecuritySpec;
import kz.kacd.sso.realmcontroller.k8s.crd.model.session.SessionsSpec;
import kz.kacd.sso.realmcontroller.k8s.crd.model.themes.ThemesSpec;
import kz.kacd.sso.realmcontroller.k8s.crd.model.token.TokensSpec;
import kz.kacd.sso.realmcontroller.k8s.model.KeycloakRealm;
import kz.kacd.sso.realmcontroller.k8s.model.SecretData;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

import static kz.kacd.sso.realmcontroller.util.ValueUtils.defaulted;
import static kz.kacd.sso.realmcontroller.util.ValueUtils.duration;

@RequiredArgsConstructor
public class RealmBuilder {

    private static final String ADMIN_EVENTS_EXPIRATION = "adminEventsExpiration";

    private final RealmRepresentation target;

    public Realm buildFrom(KeycloakRealm source, Map<String, SecretData> secrets) {
        var spec = source.source().getSpec();
        target.setEnabled(true);
        return this.withNameAndSsl(source.getName(), spec)
                .withLogin(spec.getLogin())
                .withEmail(spec.getEmail(), secrets)
                .withThemes(spec.getThemes())
                .withEvents(spec.getEvents())
                .withI17n(spec.getLocalization())
                .withSecurity(spec.getSecurity())
                .withSessions(spec.getSessions())
                .withTokens(spec.getTokens())
                .withDefaultRole()
                .build();
    }

    private RealmBuilder withNameAndSsl(String name, RealmSpec spec) {
        target.setDisplayName(defaulted(spec.getDisplayedName(), name));
        target.setDisplayNameHtml("<div class=\"kc-logo-text\"><span>" + target.getDisplayName() + "</span></div>");
        target.setSslRequired(convertSsl(spec.getRequireSsl()));
        return this;
    }

    private String convertSsl(String source) {
        if (source == null) {
            return "EXTERNAL";
        }

        return switch (source) {
            case RealmSpec.SSL_ALL -> "ALL";
            case RealmSpec.SSL_EXTERNAL -> "EXTERNAL";
            case RealmSpec.SSL_NONE -> "NONE";
            default -> throw new IllegalArgumentException("Invalid requireSsl value: " + source + "!");
        };
    }

    private RealmBuilder withLogin(LoginSpec spec) {
        return this.withLoginScreen(spec.getLoginScreen())
                .withLoginEmail(spec.getEmail())
                .withUserInfo(spec.getInfo());
    }

    private RealmBuilder withLoginScreen(LoginScreenSpec spec) {
        target.setRegistrationAllowed(defaulted(spec.getRegistration(), false));
        target.setResetPasswordAllowed(defaulted(spec.getForgotPassword(), false));
        target.setRememberMe(defaulted(spec.getRememberMe(), false));
        return this;
    }

    private RealmBuilder withLoginEmail(LoginEmailSpec spec) {
        target.setRegistrationEmailAsUsername(defaulted(spec.getEmailAsUsername(), false));
        target.setLoginWithEmailAllowed(defaulted(spec.getLoginWithEmail(), true));
        target.setDuplicateEmailsAllowed(defaulted(spec.getDuplicatesEmails(), false));
        target.setVerifyEmail(defaulted(spec.getVerifyEmail(), false));
        return this;
    }

    private RealmBuilder withUserInfo(LoginUserInfoSpec spec) {
        target.setEditUsernameAllowed(defaulted(spec.getEditUsername(), false));
        return this;
    }

    private RealmBuilder withEmail(EmailSpec spec, Map<String, SecretData> secrets) {
        target.setSmtpServer(
                new SMTPServerBuilder()
                        .withFrom(spec.getFrom())
                        .withHostAndPort(spec.getSmtpHost(), spec.getSmtpPort())
                        .withSsl(spec.getEncryption())
                        .withAuth(spec.getAuthentication(), secrets)
                        .build()
        );
        return this;
    }

    private RealmBuilder withThemes(ThemesSpec spec) {
        target.setLoginTheme(spec.getLogin());
        target.setAccountTheme(spec.getAccount());
        target.setAdminTheme(spec.getAdmin());
        target.setEmailTheme(spec.getEmail());
        return this;
    }

    private RealmBuilder withEvents(EventsSpec spec) {
        target.setEventsListeners(spec.getEventListeners());
        return this.withUserEvents(spec.getUserEvents())
                .withAdminEvents(spec.getAdminEvents());
    }

    private RealmBuilder withUserEvents(UserEventsSpec spec) {
        target.setEventsEnabled(defaulted(spec.getSaveEvents(), false));
        if (target.isEventsEnabled()) {
            target.setEventsExpiration(duration(defaulted(spec.getExpiration(), "7d")));
            target.setEnabledEventTypes(defaulted(spec.getSavedTypes(), UserEventsSpec.DEFAULT_EVENTS));
        }
        return this;
    }

    private RealmBuilder withAdminEvents(AdminEventsSpec spec) {
        target.setAdminEventsEnabled(defaulted(spec.getSaveEvents(), false));
        target.setAdminEventsDetailsEnabled(defaulted(spec.getIncludeRepresentation(), false));
        if (target.getAttributes() == null) {
            target.setAttributes(new HashMap<>());
        }
        target.getAttributes().put(
                ADMIN_EVENTS_EXPIRATION,
                duration(defaulted(spec.getExpiration(), "7d")).toString()
        );
        return this;
    }

    private RealmBuilder withI17n(LocalizationSpec spec) {
        target.setInternationalizationEnabled(defaulted(spec.getI17n(), false));
        if (target.isInternationalizationEnabled()) {
            target.setSupportedLocales(new HashSet<>(spec.getSupportedLocales()));
            target.setDefaultLocale(spec.getDefaultLocale());
        }
        return this;
    }

    private RealmBuilder withSecurity(SecuritySpec spec) {
        target.setBruteForceProtected(defaulted(spec.getBruteForce().getEnabled(), false));
        if (target.isBruteForceProtected()) {
            target.setFailureFactor(defaulted(spec.getBruteForce().getMaxLoginFailures(), 30));
            target.setPermanentLockout(defaulted(spec.getBruteForce().getPermanentLockout(), false));
            if (!target.isPermanentLockout()) {
                target.setWaitIncrementSeconds(
                        duration(defaulted(spec.getBruteForce().getWaitIncrement(), "15m")).intValue()
                );
                target.setMaxFailureWaitSeconds(
                        duration(defaulted(spec.getBruteForce().getMaxWait(), "1d")).intValue()
                );
                target.setMaxDeltaTimeSeconds(
                        duration(defaulted(spec.getBruteForce().getFailureResetTime(), "12h")).intValue()
                );
                target.setQuickLoginCheckMilliSeconds(defaulted(spec.getBruteForce().getQuickLoginMillis(), 1_000L));
                target.setMinimumQuickLoginWaitSeconds(
                        duration(defaulted(spec.getBruteForce().getQuickLoginWait(), "5m")).intValue()
                );
            }
        }
        return this;
    }

    private RealmBuilder withSessions(SessionsSpec spec) {
        target.setSsoSessionIdleTimeout(
                duration(defaulted(spec.getSessionIdle(), "15m")).intValue()
        );
        target.setSsoSessionMaxLifespan(
                duration(defaulted(spec.getSessionMax(), "1d")).intValue()
        );
        target.setOfflineSessionIdleTimeout(
                duration(defaulted(spec.getOfflineSessionIdle(), "2h")).intValue()
        );
        return this;
    }

    private RealmBuilder withTokens(TokensSpec spec) {
        target.setAccessTokenLifespan(
                duration(defaulted(spec.getAccessLifespan(), "15m")).intValue()
        );
        target.setAccessTokenLifespanForImplicitFlow(
                duration(defaulted(spec.getOidcAccessLifespan(), "15m")).intValue()
        );
        return this;
    }

    private RealmBuilder withDefaultRole() {
        var role = new RoleRepresentation();
        role.setId(UUID.randomUUID().toString());
        role.setName("default-roles-" + target.getRealm());
        role.setDescription("${role_default-roles}");
        role.setComposite(true);
        role.setClientRole(false);
        target.setDefaultRole(role);
        return this;
    }

    private Realm build() {
        return new Realm(target);
    }
}
