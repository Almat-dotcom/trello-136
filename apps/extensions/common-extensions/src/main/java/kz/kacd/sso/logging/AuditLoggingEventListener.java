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
        if (event.getRealmId() != null) {
            MDC.put("realmId", event.getRealmId());
        }
        if (event.getClientId() != null) {
            MDC.put("clientId", event.getClientId());
        }
        if (event.getSessionId() != null) {
            MDC.put("sessionId", event.getSessionId());
        }
        if (event.getUserId() != null) {
            MDC.put("userId", event.getUserId());
        }
        if (event.getIpAddress() != null) {
            MDC.put("ipAddress", event.getIpAddress());
        }
        if (event.getError() != null) {
            MDC.put("error", event.getError());
        }
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
        if (event.getRealmId() != null) {
            MDC.put("realmId", event.getRealmId());
        }
        if (event.getResourcePath() != null) {
            MDC.put("resourcePath", event.getResourcePath());
        }
        if (event.getOperationType() != null) {
            MDC.put("operationType", event.getOperationType());
        }
        if (event.getError() != null) {
            MDC.put("error", event.getError());
        }
        if (event.getAuthDetails() != null) {
            if (event.getAuthDetails().getRealmId() != null) {
                MDC.put("authRealmId", event.getAuthDetails().getRealmId());
            }
            if (event.getAuthDetails().getClientId() != null) {
                MDC.put("authClientId", event.getAuthDetails().getClientId());
            }
            if (event.getAuthDetails().getUserId() != null) {
                MDC.put("authUserId", event.getAuthDetails().getUserId());
            }
            if (event.getAuthDetails().getIpAddress() != null) {
                MDC.put("authIpAddress", event.getAuthDetails().getIpAddress());
            }
        }
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
