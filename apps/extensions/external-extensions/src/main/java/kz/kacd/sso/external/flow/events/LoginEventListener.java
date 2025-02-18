package kz.kacd.sso.external.flow.events;

import org.jboss.logging.Logger;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

import java.time.Instant;

public class LoginEventListener implements EventListenerProvider {

    private static final Logger LOG = Logger.getLogger(LoginEventListener.class);

    private final KeycloakSession session;

    public LoginEventListener(KeycloakSession session) {
        this.session = session;
    }

    @Override
    public void onEvent(Event event) {
        if ("LOGIN".equals(event.getType().toString())) {
            String ipAddress = event.getIpAddress();
            Instant loginTime = Instant.ofEpochMilli(event.getTime() * 1000);

            LOG.infof("Zhans User logged in. IP: %s, Login Time: %s", ipAddress, loginTime);

            String userId = event.getUserId();
            if (userId != null) {
                RealmModel realm = session.getContext().getRealm();
                UserModel user = session.users().getUserById(realm, userId);
                if (user != null) {
                    user.setSingleAttribute("lastLoginTime", loginTime.toString());
                    user.setSingleAttribute("lastLoginIP", ipAddress);
                }
            }
        }
    }

    @Override
    public void onEvent(AdminEvent adminEvent, boolean includeRepresentation) {

    }

    @Override
    public void close() {

    }
}
