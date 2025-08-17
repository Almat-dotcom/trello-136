package kz.kacd.sso.resource.cors;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.PreMatching;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

@Provider
@PreMatching
@Priority(Priorities.AUTHENTICATION - 10)
public class PreflightCorsFilter implements ContainerRequestFilter {

    private static final Logger log = Logger.getLogger(PreflightCorsFilter.class);

    @Override
    public void filter(ContainerRequestContext requestContext) {
        if ("OPTIONS".equalsIgnoreCase(requestContext.getMethod())) {
            String origin = requestContext.getHeaderString("Origin");
            Response response = Response.noContent()
                    .header(Cors.ACCESS_CONTROL_ALLOW_ORIGIN, origin == null ? Cors.ACCESS_CONTROL_ALLOW_ORIGIN_WILDCARD : origin)
                    .header("Vary", "Origin")
                    .header(Cors.ACCESS_CONTROL_ALLOW_METHODS, String.join(",", CorsResource.METHODS))
                    .header(Cors.ACCESS_CONTROL_ALLOW_HEADERS, Cors.DEFAULT_ALLOW_HEADERS + ", " + Cors.AUTHORIZATION_HEADER + ", Content-Type")
                    .header(Cors.ACCESS_CONTROL_EXPOSE_HEADERS, "Location")
                    .header(Cors.ACCESS_CONTROL_MAX_AGE, Cors.DEFAULT_MAX_AGE)
                    .header(Cors.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true")
                    .build();
            log.infof("Handled global CORS preflight for path=%s", requestContext.getUriInfo().getPath());
            requestContext.abortWith(response);
        }
    }
}


