package kz.kacd.sso.resource.cors;

import jakarta.ws.rs.OPTIONS;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;
import org.jboss.resteasy.spi.HttpRequest;

public class CorsResource {
    public static final String[] METHODS = {
            "GET", "HEAD", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
    };
    private static final Logger log = Logger.getLogger(CorsResource.class);
    private final HttpRequest request;

    public CorsResource(HttpRequest request) {
        this.request = request;
    }

    @OPTIONS
    @Path("{any:.*}")
    public Response preflight() {
        log.debug("CORS OPTIONS preflight request");
        return Cors.add(request, Response.ok()).auth().allowedMethods(METHODS).preflight().build();
    }
}
