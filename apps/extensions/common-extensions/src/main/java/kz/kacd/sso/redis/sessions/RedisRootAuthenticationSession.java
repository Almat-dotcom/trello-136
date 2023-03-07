package kz.kacd.sso.redis.sessions;

import kz.kacd.sso.redis.sessions.entities.CommonSessionEntity;
import kz.kacd.sso.redis.sessions.entities.RootSessionEntity;
import kz.kacd.sso.redis.sessions.repository.SessionRepository;
import org.keycloak.common.util.Time;
import org.keycloak.models.ClientModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.sessions.AuthenticationSessionModel;
import org.keycloak.sessions.RootAuthenticationSessionModel;

import java.util.HashMap;
import java.util.Map;

public class RedisRootAuthenticationSession implements RootAuthenticationSessionModel {

    private final RootSessionEntity entity;
    private final RealmModel realm;
    private final SessionRepository sessionRepository;
    private final KeycloakSession keycloakSession;

    public RedisRootAuthenticationSession(
            RootSessionEntity entity,
            RealmModel realm,
            SessionRepository sessionRepository,
            KeycloakSession keycloakSession
    ) {
        this.entity = entity;
        this.realm = realm;
        this.sessionRepository = sessionRepository;
        this.keycloakSession = keycloakSession;
    }

    RootSessionEntity getEntity() {
        return entity;
    }

    @Override
    public String getId() {
        return entity.getId();
    }

    @Override
    public RealmModel getRealm() {
        return realm;
    }

    @Override
    public int getTimestamp() {
        return entity.getTimestamp();
    }

    @Override
    public Map<String, AuthenticationSessionModel> getAuthenticationSessions() {
        Map<String, AuthenticationSessionModel> result = new HashMap<>();
        entity.getChildren().forEach((k, v) ->
                result.put(k, new SessionManager(this, keycloakSession).wrap(k, v))
        );
        return result;
    }

    @Override
    public AuthenticationSessionModel getAuthenticationSession(ClientModel client, String tabId) {
        if (client == null || tabId == null) {
            return null;
        }

        CommonSessionEntity result = entity.getChildren().get(tabId);
        if (result == null || !result.getClientId().equals(client.getId())) {
            return null;
        }
        return new SessionManager(this, keycloakSession).wrap(tabId, result);
    }

    @Override
    public AuthenticationSessionModel createAuthenticationSession(ClientModel client) {
        return new SessionManager(this, keycloakSession).createSession(client);
    }

    @Override
    public void removeAuthenticationSessionByTabId(String tabId) {
        new SessionManager(this, keycloakSession).removeChild(tabId);
    }

    @Override
    public void restartSession(RealmModel realm) {
        entity.getChildren().clear();
        setTimestamp(Time.currentTime());
    }

    @Override
    public void setTimestamp(int timestamp) {
        entity.setTimestamp(timestamp);
        save();
    }

    void save() {
        sessionRepository.persist(realm.getId(), entity);
    }

    void deleteSelf() {
        sessionRepository.delete(realm.getId(), entity.getId());
    }
}
