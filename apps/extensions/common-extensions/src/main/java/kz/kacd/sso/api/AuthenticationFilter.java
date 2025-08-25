package kz.kacd.sso.api;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

import java.io.IOException;

@Provider
public class AuthenticationFilter implements ContainerRequestFilter {
    private static final Logger log = Logger.getLogger(AuthenticationFilter.class);
    private static final String API_KEY_HEADER = "X-API-Key";
    private static final String EXPECTED_API_KEY = System.getProperty("fabric8.api.key", "fabric8-secret-key-2025");

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String path = requestContext.getUriInfo().getPath();
        
        // Skip authentication for health checks and other non-API endpoints
        if (path == null || !path.contains("k8s-")) {
            return;
        }
        
        String apiKey = requestContext.getHeaderString(API_KEY_HEADER);
        
        if (apiKey == null || !EXPECTED_API_KEY.equals(apiKey)) {
            log.warnf("Invalid or missing API key for request to: %s", path);
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\":\"Invalid or missing API key\"}")
                    .build());
            return;
        }
        
        log.debugf("Valid API key provided for request to: %s", path);
    }
}



