package kz.kacd.sso.metrics.event;

import com.google.auto.service.AutoService;
import io.micrometer.core.instrument.Tag;
import kz.kacd.sso.metrics.MetricsRegistryProvider;
import org.keycloak.Config;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.models.*;

import java.util.ArrayList;
import java.util.List;

@AutoService(EventListenerProviderFactory.class)
public class MetricsEventListener implements EventListenerProvider, EventListenerProviderFactory {
    public static final String PROVIDER_ID = "metrics-listener";

    private static final String UNDEFINED = "undefined";

    private final KeycloakSession session;

    public MetricsEventListener() {
        this.session = null;
    }

    public MetricsEventListener(KeycloakSession session) {
        this.session = session;
    }

    @Override
    public void onEvent(Event event) {
        List<Tag> tags = new ArrayList<>();

        tags.add(Tag.of("event_type", event.getType().name()));
        RealmModel realm = getRealm(event.getRealmId());
        tags.add(Tag.of("realm", realm != null ? realm.getName() : UNDEFINED));
        String clientId;
        if (realm != null) {
            clientId = getClientId(realm, event.getClientId());
        } else {
            clientId = UNDEFINED;
        }
        tags.add(Tag.of("client", clientId));
        String username;
        if (realm != null) {
            username = getUsername(realm, event.getUserId());
        } else {
            username = UNDEFINED;
        }
        tags.add(Tag.of("user", username));
        tags.add(Tag.of("session_id", event.getSessionId() != null ? event.getSessionId() : UNDEFINED));
        tags.add(Tag.of("ip_address", event.getIpAddress() != null ? event.getIpAddress() : UNDEFINED));
        tags.add(Tag.of("outcome", event.getError() != null ? "ERROR" : "SUCCESS"));

        if (session != null) {
            session.getProvider(MetricsRegistryProvider.class).provide()
                    .counter("keycloak_user_events_count", tags)
                    .increment();
        }
    }

    @Override
    public void onEvent(AdminEvent event, boolean includeRepresentation) {
        List<Tag> tags = new ArrayList<>();

        RealmModel realm = getRealm(event.getAuthDetails().getRealmId());
        tags.add(Tag.of("realm", realm != null ? realm.getName() : UNDEFINED));
        String clientId;
        if (realm != null) {
            clientId = getClientId(realm, event.getAuthDetails().getClientId());
        } else {
            clientId = UNDEFINED;
        }
        tags.add(Tag.of("client", clientId));
        String username;
        if (realm != null) {
            username = getUsername(realm, event.getAuthDetails().getUserId());
        } else {
            username = UNDEFINED;
        }
        tags.add(Tag.of("user", username));
        tags.add(Tag.of(
                "ip_address",
                event.getAuthDetails().getIpAddress() != null ? event.getAuthDetails().getIpAddress() : UNDEFINED)
        );
        RealmModel targetRealm = getRealm(event.getRealmId());
        tags.add(Tag.of("target_realm", targetRealm != null ? targetRealm.getName() : UNDEFINED));
        tags.add(Tag.of("resource", event.getResourcePath() != null ? event.getResourcePath() : UNDEFINED));
        tags.add(Tag.of("operation", event.getOperationType() != null ? event.getOperationType().name() : UNDEFINED));
        tags.add(Tag.of("resource_uri", event.getResourcePath() != null ? event.getResourcePath() : UNDEFINED));
        tags.add(Tag.of("outcome", event.getError() != null ? "ERROR" : "SUCCESS"));

        if (session != null) {
            session.getProvider(MetricsRegistryProvider.class).provide()
                    .counter("keycloak_admin_events_count", tags)
                    .increment();
        }
    }

    private RealmModel getRealm(String id) {
        if (id == null || session == null) {
            return null;
        }

        return session.realms().getRealm(id);
    }

    private String getClientId(RealmModel realm, String id) {
        if (id == null || session == null) {
            return UNDEFINED;
        }

        ClientModel client = session.clients().getClientById(realm, id);
        return client == null ? UNDEFINED : client.getClientId();
    }

    private String getUsername(RealmModel realm, String userId) {
        if (userId == null || session == null) {
            return UNDEFINED;
        }

        UserModel user = session.users().getUserById(realm, userId);
        return user == null ? UNDEFINED : user.getUsername();
    }

    @Override
    public EventListenerProvider create(KeycloakSession session) {
        return new MetricsEventListener(session);
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
    public String getId() {
        return PROVIDER_ID;
    }
}
