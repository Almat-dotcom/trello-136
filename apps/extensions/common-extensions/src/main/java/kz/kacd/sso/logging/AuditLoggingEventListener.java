package kz.kacd.sso.logging;

import com.google.auto.service.AutoService;
import org.jboss.logging.Logger;
import org.jboss.logging.MDC;
import org.keycloak.Config;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

@AutoService(EventListenerProviderFactory.class)
public class AuditLoggingEventListener implements EventListenerProvider, EventListenerProviderFactory {
    public static final String PROVIDER_ID = "audit-logging";

    private static final Logger log = Logger.getLogger(PROVIDER_ID);

    @Override
    public void onEvent(Event event) {
        MDC.clear();
        MDC.put("realmId", event.getRealmId());
        MDC.put("clientId", event.getClientId());
        MDC.put("sessionId", event.getSessionId());
        MDC.put("userId", event.getUserId());
        MDC.put("ipAddress", event.getIpAddress());
        MDC.put("error", event.getError() != null ? event.getError() : "");
        MDC.put("type", event.getType());
        MDC.put("time", event.getTime());
        event.getDetails().forEach((k, v) -> {
            if (v != null) MDC.put(k, v);
        });
        log.info("Audit event.");
    }

    @Override
    public void onEvent(AdminEvent event, boolean includeRepresentation) {
        MDC.clear();
        MDC.put("realmId", event.getRealmId());
        MDC.put("resourcePath", event.getResourcePath());
        MDC.put("operationType", event.getOperationType());
        MDC.put("error", event.getError() != null ? event.getError() : "");
        MDC.put("authRealmId", event.getAuthDetails().getRealmId());
        MDC.put("authClientId", event.getAuthDetails().getClientId());
        MDC.put("authUserId", event.getAuthDetails().getUserId());
        MDC.put("authIpAddress", event.getAuthDetails().getIpAddress());
        MDC.put("time", event.getTime());
        log.info("Audit event.");
    }

    @Override
    public EventListenerProvider create(KeycloakSession session) {
        return this;
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
        // nothing to do
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }
}
