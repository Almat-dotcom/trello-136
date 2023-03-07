package kz.kacd.sso.redis.sessions;

import org.jboss.logging.Logger;
import org.keycloak.models.ClientModel;
import org.keycloak.models.RealmModel;
import org.keycloak.sessions.AuthenticationSessionCompoundId;
import org.keycloak.sessions.AuthenticationSessionProvider;
import org.keycloak.sessions.RootAuthenticationSessionModel;

import java.util.Map;

import static kz.kacd.sso.redis.sessions.util.ModelUtils.generateSessionId;

public class RedisAuthenticationSessionProvider implements AuthenticationSessionProvider {
    private static final Logger log = Logger.getLogger(RedisAuthenticationSessionProvider.class);

    private final RootSessionManager sessionManager;

    public RedisAuthenticationSessionProvider(RootSessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public RootAuthenticationSessionModel createRootAuthenticationSession(RealmModel realm) {
        String id = generateSessionId();
        return createRootAuthenticationSession(realm, id);
    }

    @Override
    public RootAuthenticationSessionModel createRootAuthenticationSession(RealmModel realm, String id) {
        return sessionManager.create(realm, id);
    }

    @Override
    public RootAuthenticationSessionModel getRootAuthenticationSession(
            RealmModel realm,
            String authenticationSessionId
    ) {
        return sessionManager.get(realm, authenticationSessionId);
    }

    @Override
    public void removeRootAuthenticationSession(
            RealmModel realm,
            RootAuthenticationSessionModel authenticationSession
    ) {
        sessionManager.remove(realm, authenticationSession.getId());
    }

    @Override
    public void removeAllExpired() {
        // Nothing to do
    }

    @Override
    public void removeExpired(RealmModel realm) {
        // Nothing to do
    }

    @Override
    public void onRealmRemoved(RealmModel realm) {
        // Nothing to do
    }

    @Override
    public void onClientRemoved(RealmModel realm, ClientModel client) {
        // Nothing to do
    }

    @Override
    public void updateNonlocalSessionAuthNotes(
            AuthenticationSessionCompoundId compoundId,
            Map<String, String> authNotesFragment
    ) {
        log.warn("Attempt to update non-local session auth notes!");
    }

    @Override
    public void close() {
        // Nothing to close
    }
}
