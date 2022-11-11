package kz.kacd.sso.realmcontroller.keycloak.model;

import kz.kacd.sso.realmcontroller.k8s.crd.realm.model.RealmSpec;
import kz.kacd.sso.realmcontroller.k8s.crd.realm.model.email.EmailSpec;
import kz.kacd.sso.realmcontroller.k8s.crd.realm.model.event.AdminEventsSpec;
import kz.kacd.sso.realmcontroller.k8s.crd.realm.model.event.EventsSpec;
import kz.kacd.sso.realmcontroller.k8s.crd.realm.model.event.UserEventsSpec;
import kz.kacd.sso.realmcontroller.k8s.crd.realm.model.localization.LocalizationSpec;
import kz.kacd.sso.realmcontroller.k8s.crd.realm.model.login.LoginEmailSpec;
import kz.kacd.sso.realmcontroller.k8s.crd.realm.model.login.LoginScreenSpec;
import kz.kacd.sso.realmcontroller.k8s.crd.realm.model.login.LoginSpec;
import kz.kacd.sso.realmcontroller.k8s.crd.realm.model.login.LoginUserInfoSpec;
import kz.kacd.sso.realmcontroller.k8s.crd.realm.model.security.BruteForceSpec;
import kz.kacd.sso.realmcontroller.k8s.crd.realm.model.security.SecuritySpec;
import kz.kacd.sso.realmcontroller.k8s.crd.realm.model.session.SessionsSpec;
import kz.kacd.sso.realmcontroller.k8s.crd.realm.model.themes.ThemesSpec;
import kz.kacd.sso.realmcontroller.k8s.crd.realm.model.token.TokensSpec;
import kz.kacd.sso.realmcontroller.k8s.model.KeycloakRealm;
import kz.kacd.sso.realmcontroller.k8s.model.SecretData;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;

import java.util.*;

import static kz.kacd.sso.realmcontroller.util.ValueUtils.defaulted;
import static kz.kacd.sso.realmcontroller.util.ValueUtils.duration;

@RequiredArgsConstructor
public class RealmBuilder {

    private static final String ADMIN_EVENTS_EXPIRATION = "adminEventsExpiration";
    private static final String FRONT_END_URL = "frontendUrl";

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
        target.setSslRequired(convertSsl(defaulted(spec.getRequireSsl(), RealmSpec.SSL_EXTERNAL)));
        if (target.getAttributes() == null) {
            target.setAttributes(new HashMap<>());
        }
        if (spec.getFrontendUrl() != null) {
            target.getAttributes().put(FRONT_END_URL, spec.getFrontendUrl());
        } else {
            target.getAttributes().remove(FRONT_END_URL);
        }
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
        var arg = spec;
        if (spec == null) {
            arg = new LoginSpec();
        }

        return this.withLoginScreen(arg.getLoginScreen())
                .withLoginEmail(arg.getEmail())
                .withUserInfo(arg.getInfo());
    }

    private RealmBuilder withLoginScreen(LoginScreenSpec spec) {
        var arg = spec;
        if (spec == null) {
            arg = new LoginScreenSpec();
        }

        target.setRegistrationAllowed(defaulted(arg.getRegistration(), false));
        target.setResetPasswordAllowed(defaulted(arg.getForgotPassword(), false));
        target.setRememberMe(defaulted(arg.getRememberMe(), false));
        return this;
    }

    private RealmBuilder withLoginEmail(LoginEmailSpec spec) {
        var arg = spec;
        if (spec == null) {
            arg = new LoginEmailSpec();
        }

        target.setRegistrationEmailAsUsername(defaulted(arg.getEmailAsUsername(), false));
        target.setLoginWithEmailAllowed(defaulted(arg.getLoginWithEmail(), true));
        target.setDuplicateEmailsAllowed(defaulted(arg.getDuplicatesEmails(), false));
        target.setVerifyEmail(defaulted(arg.getVerifyEmail(), false));
        return this;
    }

    private RealmBuilder withUserInfo(LoginUserInfoSpec spec) {
        var arg = spec;
        if (spec == null) {
            arg = new LoginUserInfoSpec();
        }

        target.setEditUsernameAllowed(defaulted(arg.getEditUsername(), false));
        return this;
    }

    private RealmBuilder withEmail(EmailSpec spec, Map<String, SecretData> secrets) {
        if (spec == null) {
            return this;
        }

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
        var arg = spec;
        if (arg == null) {
            arg = new ThemesSpec();
        }

        target.setLoginTheme(arg.getLogin());
        target.setAccountTheme(arg.getAccount());
        target.setAdminTheme(arg.getAdmin());
        target.setEmailTheme(arg.getEmail());
        return this;
    }

    private RealmBuilder withEvents(EventsSpec spec) {
        var arg = spec;
        if (spec == null) {
            arg = new EventsSpec();
        }

        target.setEventsListeners(defaulted(arg.getEventListeners(), List.of("jboss-logging")));
        return this.withUserEvents(arg.getUserEvents())
                .withAdminEvents(arg.getAdminEvents());
    }

    private RealmBuilder withUserEvents(UserEventsSpec spec) {
        var arg = spec;
        if (spec == null) {
            arg = new UserEventsSpec();
        }

        target.setEventsEnabled(defaulted(arg.getSaveEvents(), false));
        if (target.isEventsEnabled()) {
            target.setEventsExpiration(duration(defaulted(arg.getExpiration(), "7d")));
            target.setEnabledEventTypes(defaulted(arg.getSavedTypes(), UserEventsSpec.DEFAULT_EVENTS));
        }
        return this;
    }

    private RealmBuilder withAdminEvents(AdminEventsSpec spec) {
        var arg = spec;
        if (spec == null) {
            arg = new AdminEventsSpec();
        }

        target.setAdminEventsEnabled(defaulted(arg.getSaveEvents(), false));
        target.setAdminEventsDetailsEnabled(defaulted(arg.getIncludeRepresentation(), false));
        if (target.getAttributes() == null) {
            target.setAttributes(new HashMap<>());
        }
        target.getAttributes().put(
                ADMIN_EVENTS_EXPIRATION,
                duration(defaulted(arg.getExpiration(), "7d")).toString()
        );
        return this;
    }

    private RealmBuilder withI17n(LocalizationSpec spec) {
        var arg = spec;
        if (spec == null) {
            arg = new LocalizationSpec();
        }

        target.setInternationalizationEnabled(defaulted(arg.getI17n(), false));
        if (target.isInternationalizationEnabled()) {
            target.setSupportedLocales(new HashSet<>(defaulted(arg.getSupportedLocales(), List.of())));
            target.setDefaultLocale(defaulted(arg.getDefaultLocale(), "en"));
        }
        return this;
    }

    private RealmBuilder withSecurity(SecuritySpec spec) {
        var arg = spec;
        if (spec == null) {
            arg = new SecuritySpec();
            arg.setBruteForce(new BruteForceSpec());
        }

        target.setBruteForceProtected(defaulted(arg.getBruteForce().getEnabled(), false));
        if (target.isBruteForceProtected()) {
            target.setFailureFactor(defaulted(arg.getBruteForce().getMaxLoginFailures(), 30));
            target.setPermanentLockout(defaulted(arg.getBruteForce().getPermanentLockout(), false));
            if (!target.isPermanentLockout()) {
                target.setWaitIncrementSeconds(
                        duration(defaulted(arg.getBruteForce().getWaitIncrement(), "15m")).intValue()
                );
                target.setMaxFailureWaitSeconds(
                        duration(defaulted(arg.getBruteForce().getMaxWait(), "1d")).intValue()
                );
                target.setMaxDeltaTimeSeconds(
                        duration(defaulted(arg.getBruteForce().getFailureResetTime(), "12h")).intValue()
                );
                target.setQuickLoginCheckMilliSeconds(defaulted(arg.getBruteForce().getQuickLoginMillis(), 1_000L));
                target.setMinimumQuickLoginWaitSeconds(
                        duration(defaulted(arg.getBruteForce().getQuickLoginWait(), "5m")).intValue()
                );
            }
        }
        return this;
    }

    private RealmBuilder withSessions(SessionsSpec spec) {
        var arg = spec;
        if (spec == null) {
            arg = new SessionsSpec();
        }

        target.setSsoSessionIdleTimeout(
                duration(defaulted(arg.getSessionIdle(), "15m")).intValue()
        );
        target.setSsoSessionMaxLifespan(
                duration(defaulted(arg.getSessionMax(), "1d")).intValue()
        );
        target.setOfflineSessionIdleTimeout(
                duration(defaulted(arg.getOfflineSessionIdle(), "2h")).intValue()
        );
        return this;
    }

    private RealmBuilder withTokens(TokensSpec spec) {
        var arg = spec;
        if (spec == null) {
            arg = new TokensSpec();
        }

        target.setAccessTokenLifespan(
                duration(defaulted(arg.getAccessLifespan(), "15m")).intValue()
        );
        target.setAccessTokenLifespanForImplicitFlow(
                duration(defaulted(arg.getOidcAccessLifespan(), "15m")).intValue()
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
