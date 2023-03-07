package kz.kacd.sso.redis.sessions;

import kz.kacd.sso.redis.client.RedisCacheProvider;
import kz.kacd.sso.redis.sessions.entities.RootSessionEntity;
import kz.kacd.sso.redis.sessions.repository.SessionRepository;
import org.jboss.logging.Logger;
import org.keycloak.common.util.Time;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.utils.SessionExpiration;

public class RootSessionManager {
    private static final Logger log = Logger.getLogger(RootSessionManager.class);

    private final KeycloakSession keycloakSession;

    public RootSessionManager(KeycloakSession keycloakSession) {
        this.keycloakSession = keycloakSession;
    }

    public RedisRootAuthenticationSession create(RealmModel realm, String id) {
        log.debugf(
                "Creating new root authentication session with realm {} and id {} ...",
                realm.getName(),
                id
        );
        RootSessionEntity entity = new RootSessionEntity(id, realm.getId(), Time.currentTime());

        int expirationSeconds = SessionExpiration.getAuthSessionLifespan(realm);
        SessionRepository repository = createRepository(expirationSeconds);

        repository.persist(realm.getId(), entity);
        return wrap(entity, realm, repository);
    }

    public RedisRootAuthenticationSession get(RealmModel realm, String id) {
        log.debugf(
                "Getting root authentication session by realm {} and id {} ...",
                realm.getName(),
                id
        );
        int expirationSeconds = SessionExpiration.getAuthSessionLifespan(realm);
        SessionRepository repository = createRepository(expirationSeconds);

        RootSessionEntity entity = repository.get(realm.getId(), id);
        if (entity == null) {
            return null;
        }

        return wrap(entity, realm, repository);
    }

    private RedisRootAuthenticationSession wrap(
            RootSessionEntity entity,
            RealmModel realm,
            SessionRepository repository
    ) {
        return new RedisRootAuthenticationSession(
                entity,
                realm,
                repository,
                keycloakSession
        );
    }

    public void remove(RealmModel realm, String id) {
        log.debugf(
                "Removing root authentication session for realm {} with id {} ...",
                realm.getName(),
                id
        );

        int expirationSeconds = SessionExpiration.getAuthSessionLifespan(realm);
        SessionRepository repository = createRepository(expirationSeconds);

        repository.delete(realm.getId(), id);
    }

    private SessionRepository createRepository(int expirationSeconds) {
        RedisCacheProvider cacheProvider = keycloakSession.getProvider(RedisCacheProvider.class);
        return new SessionRepository(expirationSeconds, cacheProvider);
    }
}
