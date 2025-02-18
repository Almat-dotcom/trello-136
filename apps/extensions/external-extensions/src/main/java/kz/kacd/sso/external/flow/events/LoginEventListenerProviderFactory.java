package kz.kacd.sso.external.flow.events;

import org.keycloak.Config;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

public class LoginEventListenerProviderFactory implements EventListenerProviderFactory {

    public static final String ID = "login-event-listener";

    @Override
    public EventListenerProvider create(KeycloakSession session) {
        return new LoginEventListener(session);
    }

    @Override
    public void init(Config.Scope config) {
        // Не требуется
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        // Не требуется
    }

    @Override
    public void close() {
        // Не требуется
    }

    @Override
    public String getId() {
        return ID;
    }
}
