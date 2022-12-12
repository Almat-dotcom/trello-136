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
        configureSso(realm, source.getSso());
        configureClient(realm, source.getClient());
        configureOffline(realm, source.getOffline());
    }

    private void configureSso(RealmModel realm, Sso source) {
        realm.setSsoSessionIdleTimeout(duration(defaulted(source.getSessionIdle(), "15m")).intValue());
        realm.setSsoSessionMaxLifespan(duration(defaulted(source.getSessionMax(), "1d")).intValue());
        realm.setSsoSessionIdleTimeoutRememberMe(duration(defaulted(source.getRememberMeSessionIdle(), "2h")).intValue());
    }

    private void configureClient(RealmModel realm, Client source) {
        realm.setClientSessionIdleTimeout(duration(defaulted(source.getSessionIdle(), "10s")).intValue());
        realm.setClientSessionMaxLifespan(duration(defaulted(source.getSessionMax(), "5m")).intValue());
    }

    private void configureOffline(RealmModel realm, Offline source) {
        realm.setOfflineSessionIdleTimeout(duration(defaulted(source.getSessionIdle(), "1d")).intValue());
        realm.setOfflineSessionMaxLifespan(duration(defaulted(source.getSessionMax(), "360d")).intValue());
    }
}
