package kz.kacd.sso.metrics.resource;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import kz.kacd.sso.metrics.MetricsRegistryProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.services.resource.RealmResourceProvider;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class MetricsEndpoint implements RealmResourceProvider {

    public static final String ID = "metrics";

    private final KeycloakSession session;

    public MetricsEndpoint(KeycloakSession session) {
        this.session = session;
    }

    @Override
    public Object getResource() {
        return this;
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response get(@Context HttpHeaders headers) {
        if (!headers.getRequestHeader("x-forwarded-host").isEmpty()) {
            if (!System.getenv("KC_HOSTNAME").equals(headers.getRequestHeader("x-forwarded-host").get(0))) {
                return Response.status(403).build();
            }
        }

        MeterRegistry registry = session.getProvider(MetricsRegistryProvider.class).provide();
        if (!(registry instanceof PrometheusMeterRegistry)) {
            return Response.status(404).build();
        }

        PrometheusMeterRegistry prom = (PrometheusMeterRegistry) registry;
        String response = prom.scrape();
        return Response.ok(response).build();
    }

    @Override
    public void close() {
        // Nothing to do
    }
}
