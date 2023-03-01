package kz.kacd.sso.realm.config.model;

import kz.kacd.sso.v1.realmspec.Sessions;
import kz.kacd.sso.v1.realmspec.sessions.Client;
import kz.kacd.sso.v1.realmspec.sessions.Offline;
import kz.kacd.sso.v1.realmspec.sessions.Sso;
import org.keycloak.models.RealmModel;

import static kz.kacd.sso.util.ValueUtils.defaulted;
import static kz.kacd.sso.util.ValueUtils.duration;

public class SessionsConfigurer {

    private final Sessions source;

    public SessionsConfigurer(Sessions source) {
        this.source = source;
    }

    public void configure(RealmModel realm) {
        configureBase(realm);
        configureSso(realm, source.getSso());
        configureClient(realm, source.getClient());
        configureOffline(realm, source.getOffline());
    }

    private void configureBase(RealmModel realm) {
        realm.setAccessCodeLifespan(duration("15s").intValue());
        realm.setAccessCodeLifespanLogin(duration("15s").intValue());
        realm.setAccessCodeLifespanUserAction(duration("5m").intValue());
        realm.setActionTokenGeneratedByUserLifespan(duration("5m").intValue());
        realm.setActionTokenGeneratedByAdminLifespan(duration("12h").intValue());
    }

    private void configureSso(RealmModel realm, Sso source) {
        Sso spec = source;
        if (spec == null) {
            spec = new Sso();
        }

        realm.setSsoSessionIdleTimeout(duration(defaulted(spec.getSessionIdle(), "15m")).intValue());
        realm.setSsoSessionMaxLifespan(duration(defaulted(spec.getSessionMax(), "1d")).intValue());
        realm.setSsoSessionIdleTimeoutRememberMe(duration(defaulted(spec.getRememberMeSessionIdle(), "2h")).intValue());
    }

    private void configureClient(RealmModel realm, Client source) {
        Client spec = source;
        if (spec == null) {
            spec = new Client();
        }

        realm.setClientSessionIdleTimeout(duration(defaulted(spec.getSessionIdle(), "15m")).intValue());
        realm.setClientSessionMaxLifespan(duration(defaulted(spec.getSessionMax(), "15m")).intValue());
    }

    private void configureOffline(RealmModel realm, Offline source) {
        Offline spec = source;
        if (spec == null) {
            spec = new Offline();
        }

        realm.setOfflineSessionIdleTimeout(duration(defaulted(spec.getSessionIdle(), "1d")).intValue());
        realm.setOfflineSessionMaxLifespan(duration(defaulted(spec.getSessionMax(), "360d")).intValue());
    }
}
