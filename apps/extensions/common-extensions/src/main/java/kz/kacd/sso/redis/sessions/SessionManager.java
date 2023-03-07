package kz.kacd.sso.redis.sessions;

import kz.kacd.sso.redis.sessions.entities.CommonSessionEntity;
import org.jboss.logging.Logger;
import org.keycloak.common.util.Time;
import org.keycloak.models.ClientModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.sessions.AuthenticationSessionModel;

import java.util.Comparator;
import java.util.Map;
import java.util.Objects;

import static kz.kacd.sso.redis.sessions.util.ModelUtils.generateSessionId;

public class SessionManager {
    private static final Logger log = Logger.getLogger(SessionManager.class);

    private static final int SESSIONS_LIMIT = 300;

    private final RedisRootAuthenticationSession root;
    private final KeycloakSession keycloakSession;

    public SessionManager(RedisRootAuthenticationSession root, KeycloakSession keycloakSession) {
        this.root = root;
        this.keycloakSession = keycloakSession;
    }

    public AuthenticationSessionModel createSession(ClientModel client) {
        Objects.requireNonNull(client);

        Map<String, CommonSessionEntity> children = root.getEntity().getChildren();
        if (children.size() >= SESSIONS_LIMIT) {
            String id = children.entrySet().stream()
                    .min(Comparator.comparingInt(it -> it.getValue().getTimestamp()))
                    .map(Map.Entry::getKey)
                    .orElse(null);

            if (id != null) {
                log.debugf(
                        "Reached limit {} of active sessions per a root session. Removing oldest one {} ...",
                        root.getId(),
                        id
                );

                root.getEntity().getChildren().remove(id);
                save();
            }
        }

        CommonSessionEntity child = new CommonSessionEntity();
        child.setClientId(client.getId());
        child.setTimestamp(Time.currentTime());
        String tabId = generateSessionId();
        root.getEntity().getChildren().put(tabId, child);
        root.getEntity().setTimestamp(child.getTimestamp());

        log.debugf(
                "Creating new session {} for root session {} in realm {} ...",
                tabId,
                root.getId(),
                root.getRealm().getName()
        );
        save();

        return wrap(tabId, child);
    }

    public void removeChild(String tabId) {
        if (root.getEntity().getChildren().remove(tabId) != null) {
            if (root.getEntity().getChildren().isEmpty()) {
                root.deleteSelf();
            } else {
                root.setTimestamp(Time.currentTime());
            }
        }
    }

    public RedisAuthenticationSession wrap(
            String key,
            CommonSessionEntity entity
    ) {
        RealmModel realm = root.getRealm();
        ClientModel client = realm.getClientById(entity.getClientId());
        UserModel user = entity.getAuthUserId() == null
                ? null
                : keycloakSession.users().getUserById(realm, entity.getAuthUserId());
        return new RedisAuthenticationSession(key, root, realm, client, user, entity, this);
    }

    public void save() {
        root.save();
    }
}
