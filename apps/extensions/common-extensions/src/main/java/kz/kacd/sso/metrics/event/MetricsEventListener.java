package kz.kacd.sso.metrics.event;

import com.google.auto.service.AutoService;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import kz.kacd.sso.metrics.MetricsRegistryProvider;
import org.keycloak.Config;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.models.*;
import org.keycloak.models.utils.KeycloakModelUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@AutoService(EventListenerProviderFactory.class)
public class MetricsEventListener implements EventListenerProvider, EventListenerProviderFactory {
    public static final String PROVIDER_ID = "metrics-listener";

    private static final String UNDEFINED = "undefined";
    private static final String ALL = "All";
    private static final String USER_EVENT = "keycloak_user_events_count";
    private static final String ADMIN_EVENT = "keycloak_admin_events_count";

    private final KeycloakSession session;

    public MetricsEventListener() {
        this.session = null;
    }

    public MetricsEventListener(KeycloakSession session) {
        this.session = session;
    }

    @Override
    public void onEvent(Event event) {
        inTransaction(s -> {
            List<Tag> tags = new ArrayList<>();

            tags.add(Tag.of("event_type", event.getType().name()));
            tags.add(Tag.of("outcome", event.getError() != null ? "ERROR" : "SUCCESS"));

            count(
                    s,
                    USER_EVENT,
                    event.getRealmId(),
                    event.getClientId(),
                    event.getUserId(),
                    event.getSessionId(),
                    event.getIpAddress(),
                    tags
            );
        });
    }

    @Override
    public void onEvent(AdminEvent event, boolean includeRepresentation) {
        inTransaction(s -> {
            List<Tag> tags = new ArrayList<>();

            RealmModel targetRealm = getRealm(s, event.getRealmId());
            tags.add(Tag.of("target_realm", targetRealm != null ? targetRealm.getName() : UNDEFINED));
            tags.add(Tag.of("resource", event.getResourcePath() != null ? event.getResourcePath() : UNDEFINED));
            tags.add(Tag.of("operation", event.getOperationType() != null ? event.getOperationType().name() : UNDEFINED));
            tags.add(Tag.of("resource_uri", event.getResourcePath() != null ? event.getResourcePath() : UNDEFINED));
            tags.add(Tag.of("outcome", event.getError() != null ? "ERROR" : "SUCCESS"));

            RealmModel authRealm = getRealm(s, event.getAuthDetails().getRealmId());
            String clientId;
            if (authRealm == null || event.getAuthDetails().getClientId() == null) {
                clientId = null;
            } else {
                ClientModel client = authRealm.getClientById(event.getAuthDetails().getClientId());
                clientId = client != null ? client.getClientId() : null;
            }

            count(
                    s,
                    ADMIN_EVENT,
                    event.getAuthDetails().getRealmId(),
                    clientId,
                    event.getAuthDetails().getUserId(),
                    null,
                    event.getAuthDetails().getIpAddress(),
                    tags
            );
        });
    }

    private void inTransaction(Consumer<KeycloakSession> consumer) {
        if (session == null) {
            return;
        }

        boolean openTransactionAfterAll = false;
        if (session.getTransactionManager().isActive()) {
            session.getTransactionManager().commit();
            openTransactionAfterAll = true;
        }

        KeycloakModelUtils.runJobInTransaction(
                session.getKeycloakSessionFactory(),
                consumer::accept
        );

        if (openTransactionAfterAll) {
            session.getTransactionManager().begin();
        }
    }

    private void count(
            KeycloakSession s,
            String metric,
            String realmId,
            String clientId,
            String userId,
            String sessionId,
            String ipAddress,
            List<Tag> tags
    ) {
        tags.add(Tag.of("ip_address", ipAddress == null ? UNDEFINED : ipAddress));
        tags.add(Tag.of("session", sessionId == null ? UNDEFINED : sessionId));

        RealmModel realm = getRealm(s, realmId);

        if (realm != null && userId != null) {
            String username = getUsername(s, realm, userId);
            tags.add(Tag.of("user", username != null ? username : UNDEFINED));
        } else {
            tags.add(Tag.of("user", UNDEFINED));
        }

        List<Tag> specifiedRealm = new ArrayList<>(tags);
        specifiedRealm.add(Tag.of("realm", realm != null ? realm.getName() : UNDEFINED));
        List<Tag> allRealm = new ArrayList<>(tags);
        allRealm.add(Tag.of("realm", ALL));

        List<Tag> specifiedRealmSpecifiedClient = new ArrayList<>(specifiedRealm);
        specifiedRealmSpecifiedClient.add(Tag.of("client", clientId == null ? UNDEFINED : clientId));
        List<Tag> specifiedRealmAllClient = new ArrayList<>(specifiedRealm);
        specifiedRealmAllClient.add(Tag.of("client", ALL));
        List<Tag> allRealmSpecifiedClient = new ArrayList<>(allRealm);
        allRealmSpecifiedClient.add(Tag.of("client", clientId == null ? UNDEFINED : clientId));
        List<Tag> allRealmAllClient = new ArrayList<>(allRealm);
        allRealmAllClient.add(Tag.of("client", ALL));

        MeterRegistry registry = s.getProvider(MetricsRegistryProvider.class).provide();
        registry.counter(metric, specifiedRealmSpecifiedClient).increment();
        registry.counter(metric, specifiedRealmAllClient).increment();
        registry.counter(metric, allRealmSpecifiedClient).increment();
        registry.counter(metric, allRealmAllClient).increment();
    }

    private RealmModel getRealm(KeycloakSession s, String id) {
        return s.realms().getRealm(id);
    }

    private String getUsername(KeycloakSession s, RealmModel realm, String userId) {
        UserModel user = s.users().getUserById(realm, userId);
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
