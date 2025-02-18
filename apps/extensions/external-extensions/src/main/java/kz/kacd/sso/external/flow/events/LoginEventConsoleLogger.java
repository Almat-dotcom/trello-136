package kz.kacd.sso.external.flow.events;

import org.jboss.logging.Logger;
import org.keycloak.events.Event;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.events.EventListenerProvider;

import java.time.Instant;

public class LoginEventConsoleLogger implements EventListenerProvider {

    private static final Logger LOG = Logger.getLogger(LoginEventConsoleLogger.class);

    @Override
    public void onEvent(Event event) {
        if ("LOGIN".equals(event.getType().toString())) {
            String ipAddress = event.getIpAddress();
            Instant loginTime = Instant.ofEpochMilli(event.getTime() * 1000);
            LOG.infof("Test User logged in. IP: %s, Login Time: %s", ipAddress, loginTime);
        }
    }

    @Override
    public void onEvent(AdminEvent adminEvent, boolean includeRepresentation) {

    }

    @Override
    public void close() {

    }
}

