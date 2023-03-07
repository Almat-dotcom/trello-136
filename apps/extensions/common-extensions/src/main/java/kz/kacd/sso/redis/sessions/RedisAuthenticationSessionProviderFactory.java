package kz.kacd.sso.redis.sessions;

import com.google.auto.service.AutoService;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.sessions.AuthenticationSessionProviderFactory;

@AutoService(AuthenticationSessionProviderFactory.class)
public class RedisAuthenticationSessionProviderFactory implements AuthenticationSessionProviderFactory {

    private static final String PROVIDER_ID = "redis";

    @Override
    public RedisAuthenticationSessionProvider create(KeycloakSession session) {
        RootSessionManager manager = new RootSessionManager(session);
        return new RedisAuthenticationSessionProvider(manager);
    }

    @Override
    public void init(Config.Scope config) {
        // Nothing to do
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        // Nothing to do
    }

    @Override
    public void close() {
        // Nothing to do
    }

    @Override
    public int order() {
        return 99;
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }
}
