package kz.kacd.sso.resource.cors;

import org.jboss.logging.Logger;
import org.jboss.resteasy.spi.HttpRequest;

import javax.ws.rs.OPTIONS;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

public class CorsResource {
    private static final Logger log = Logger.getLogger(CorsResource.class);

    public static final String[] METHODS = {
            "GET", "HEAD", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
    };

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
