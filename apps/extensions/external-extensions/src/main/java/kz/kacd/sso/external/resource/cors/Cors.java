package kz.kacd.sso.external.resource.cors;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.ResponseBuilder;
import org.jboss.logging.Logger;
import org.keycloak.common.util.CollectionUtil;
import org.keycloak.http.HttpRequest;
import org.keycloak.http.HttpResponse;
import org.keycloak.models.ClientModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.protocol.oidc.utils.WebOriginsUtils;
import org.keycloak.representations.AccessToken;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class Cors {

    public static final long   DEFAULT_MAX_AGE      = TimeUnit.HOURS.toSeconds(1);
    public static final String DEFAULT_ALLOW_METHODS = "GET, HEAD, OPTIONS";
    public static final String DEFAULT_ALLOW_HEADERS =
            "X-PINGOTHER, Origin, Accept, X-Requested-With, Content-Type, " +
                    "Access-Control-Request-Method, Access-Control-Request-Headers";

    public static final String ORIGIN_HEADER                    = "Origin";
    public static final String AUTHORIZATION_HEADER             = "Authorization";
    public static final String ACCESS_CONTROL_ALLOW_ORIGIN      = "Access-Control-Allow-Origin";
    public static final String ACCESS_CONTROL_ALLOW_METHODS     = "Access-Control-Allow-Methods";
    public static final String ACCESS_CONTROL_ALLOW_HEADERS     = "Access-Control-Allow-Headers";
    public static final String ACCESS_CONTROL_EXPOSE_HEADERS    = "Access-Control-Expose-Headers";
    public static final String ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";
    public static final String ACCESS_CONTROL_MAX_AGE           = "Access-Control-Max-Age";
    public static final String ACCESS_CONTROL_ALLOW_ORIGIN_WILDCARD = "*";
    public static final String INCLUDE_REDIRECTS = "+";

    private static final Logger logger = Logger.getLogger(Cors.class);

    private final HttpRequest request;
    private ResponseBuilder   builder;

    private Set<String> allowedOrigins;
    private Set<String> allowedMethods;
    private Set<String> exposedHeaders;

    private boolean preflight;
    private boolean auth;

    public Cors(HttpRequest request, ResponseBuilder builder) {
        this.request = request;
        this.builder = builder;
    }

    public Cors(HttpRequest request) {
        this(request, Response.ok());
    }

    public static Cors add(HttpRequest req, ResponseBuilder b) { return new Cors(req, b); }
    public static Cors add(HttpRequest req)                   { return new Cors(req);   }

    public Cors builder(ResponseBuilder b) { this.builder = b; return this; }
    public Cors preflight()  { this.preflight = true; return this; }
    public Cors auth()       { this.auth      = true; return this; }


    public Cors allowAllOrigins() {
        this.allowedOrigins = Collections.singleton(ACCESS_CONTROL_ALLOW_ORIGIN_WILDCARD);
        return this;
    }

    public Cors allowedOrigins(KeycloakSession s, ClientModel c) {
        if (c != null) this.allowedOrigins = WebOriginsUtils.resolveValidWebOrigins(s, c);
        return this;
    }

    public Cors allowedOrigins(AccessToken t) {
        if (t != null) this.allowedOrigins = t.getAllowedOrigins();
        return this;
    }

    public Cors allowedOrigins(String... o) {
        if (o != null && o.length > 0) this.allowedOrigins = new HashSet<>(Arrays.asList(o));
        return this;
    }

    public Cors allowedMethods(String... m) {
        this.allowedMethods = new HashSet<>(Arrays.asList(m));
        return this;
    }

    public Cors exposedHeaders(String... h) {
        this.exposedHeaders = new HashSet<>(Arrays.asList(h));
        return this;
    }

    public Response build() {
        String origin = request.getHttpHeaders().getRequestHeaders().getFirst(ORIGIN_HEADER);
        if (origin == null) {
            return builder.build();
        }

        if (invalidRequest(origin)) {
            logInfo("Invalid CORS request: origin {0} not in allowed origins {1}", origin, allowedOrigins);
            return builder.build();
        }

        builder.header(ACCESS_CONTROL_ALLOW_ORIGIN, origin);

        if (preflight) addAccessControl();               // Allow-Methods
        if (!preflight && exposedHeaders != null)
            builder.header(ACCESS_CONTROL_EXPOSE_HEADERS, CollectionUtil.join(exposedHeaders));

        builder.header(ACCESS_CONTROL_ALLOW_CREDENTIALS, Boolean.toString(auth));

        if (preflight) {
            addAuth();                                   // Allow-Headers
            builder.header(ACCESS_CONTROL_MAX_AGE, DEFAULT_MAX_AGE);
        }
        return builder.build();
    }

    public void build(HttpResponse resp) {
        String origin = request.getHttpHeaders().getRequestHeaders().getFirst(ORIGIN_HEADER);
        if (origin == null) {
            return;
        }

        if (invalidRequest(origin)) {
            logInfo("Invalid CORS request: origin {0} not in allowed origins {1}", origin, allowedOrigins);
            return;
        }

        resp.setHeader(ACCESS_CONTROL_ALLOW_ORIGIN, origin);

        if (preflight) addAccessControl(resp);
        if (!preflight && exposedHeaders != null)
            resp.setHeader(ACCESS_CONTROL_EXPOSE_HEADERS, CollectionUtil.join(exposedHeaders));

        resp.setHeader(ACCESS_CONTROL_ALLOW_CREDENTIALS, Boolean.toString(auth));

        if (preflight) {
            addAuth(resp);
            resp.setHeader(ACCESS_CONTROL_MAX_AGE, String.valueOf(DEFAULT_MAX_AGE));
        }
    }


    private boolean invalidRequest(String origin) {
        return !preflight &&
                (allowedOrigins == null ||
                        (!allowedOrigins.contains(origin) &&
                                !allowedOrigins.contains(ACCESS_CONTROL_ALLOW_ORIGIN_WILDCARD)));
    }

    private void logInfo(String message, Object... args) {
        if (logger.isInfoEnabled()) {
            logger.infov(message, args);
        }
    }

    private void addAccessControl() {
        if (allowedMethods != null)
            builder.header(ACCESS_CONTROL_ALLOW_METHODS, CollectionUtil.join(allowedMethods));
        else
            builder.header(ACCESS_CONTROL_ALLOW_METHODS, DEFAULT_ALLOW_METHODS);
    }

    private void addAuth() {
        String allow = auth ? DEFAULT_ALLOW_HEADERS + ", " + AUTHORIZATION_HEADER
                : DEFAULT_ALLOW_HEADERS;
        builder.header(ACCESS_CONTROL_ALLOW_HEADERS, allow);
    }

    private void addAccessControl(HttpResponse resp) {
        if (allowedMethods != null)
            resp.setHeader(ACCESS_CONTROL_ALLOW_METHODS, CollectionUtil.join(allowedMethods));
        else
            resp.setHeader(ACCESS_CONTROL_ALLOW_METHODS, DEFAULT_ALLOW_METHODS);
    }

    private void addAuth(HttpResponse resp) {
        String allow = auth ? DEFAULT_ALLOW_HEADERS + ", " + AUTHORIZATION_HEADER
                : DEFAULT_ALLOW_HEADERS;
        resp.setHeader(ACCESS_CONTROL_ALLOW_HEADERS, allow);
    }
}