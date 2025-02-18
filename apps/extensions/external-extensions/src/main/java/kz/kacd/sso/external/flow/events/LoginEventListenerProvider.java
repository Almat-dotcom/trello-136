package kz.kacd.sso.external.flow.events;

import org.jboss.logging.Logger;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.models.KeycloakSession;

public class LoginEventListenerProvider implements EventListenerProvider {
    private static final Logger LOG = Logger.getLogger(LoginEventListenerProvider.class);

    private final KeycloakSession session;

    public LoginEventListenerProvider(KeycloakSession session) {
        this.session = session;
    }

    @Override
    public void onEvent(Event event) {
        if (event.getType() == EventType.LOGIN) {
            String userId = event.getUserId();
            String ip = event.getDetails().get("ipAddress");
            long loginTimeMillis = event.getTime();

            LOG.infof("User logged in. userId=%s, ipAddress=%s, loginTime=%d", userId, ip, loginTimeMillis);
        }
    }

    @Override
    public void onEvent(org.keycloak.events.admin.AdminEvent event, boolean includeRepresentation) {

    }

    @Override
    public void close() {

    }
}
