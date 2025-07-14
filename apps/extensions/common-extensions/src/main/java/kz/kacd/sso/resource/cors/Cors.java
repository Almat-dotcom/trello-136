package kz.kacd.sso.resource.cors;

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

import java.util.*;
import java.util.concurrent.TimeUnit;

public class Cors {

    public static final long DEFAULT_MAX_AGE = TimeUnit.HOURS.toSeconds(1);
    public static final String DEFAULT_ALLOW_METHODS = "GET, HEAD, OPTIONS";
    public static final String DEFAULT_ALLOW_HEADERS =
            "X-PINGOTHER, Origin, Accept, X-Requested-With, Content-Type, " +
                    "Access-Control-Request-Method, Access-Control-Request-Headers";
    public static final String ORIGIN_HEADER = "Origin";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
    public static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
    public static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
    public static final String ACCESS_CONTROL_EXPOSE_HEADERS = "Access-Control-Expose-Headers";
    public static final String ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";
    public static final String ACCESS_CONTROL_MAX_AGE = "Access-Control-Max-Age";
    public static final String ACCESS_CONTROL_ALLOW_ORIGIN_WILDCARD = "*";
    private static final Logger logger = Logger.getLogger(Cors.class);

    private final HttpRequest request;
    private ResponseBuilder builder;
    private Set<String> allowedOrigins;
    private Set<String> allowedMethods;
    private Set<String> exposedHeaders;
    private boolean preflight;
    private boolean auth;

    public Cors(HttpRequest request, ResponseBuilder response) {
        this.request = request;
        this.builder = response;
    }

    public Cors(HttpRequest request) {
        this(request, Response.ok());
    }

    public static Cors add(HttpRequest request, ResponseBuilder response) {
        return new Cors(request, response);
    }

    public static Cors add(HttpRequest request) {
        return new Cors(request);
    }

    public Cors builder(ResponseBuilder builder) {
        this.builder = builder;
        return this;
    }

    public Cors preflight()        { this.preflight = true; return this; }
    public Cors auth()             { this.auth = true;      return this; }

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

    public Cors allowedMethods(String... m) { this.allowedMethods = new HashSet<>(Arrays.asList(m)); return this; }
    public Cors exposedHeaders(String... h) { this.exposedHeaders = new HashSet<>(Arrays.asList(h)); return this; }


    public Response build() {
        String origin = request.getHttpHeaders().getRequestHeaders().getFirst(ORIGIN_HEADER);
        if (origin == null || invalidRequest(origin)) return builder.build();

        builder.header(ACCESS_CONTROL_ALLOW_ORIGIN, origin);
        if (preflight) addAccessControl(); else if (exposedHeaders != null)
            builder.header(ACCESS_CONTROL_EXPOSE_HEADERS, CollectionUtil.join(exposedHeaders));

        builder.header(ACCESS_CONTROL_ALLOW_CREDENTIALS, Boolean.toString(auth));
        if (preflight) { addAuth(); builder.header(ACCESS_CONTROL_MAX_AGE, DEFAULT_MAX_AGE); }
        return builder.build();
    }

    public void build(HttpResponse response) {
        String origin = request.getHttpHeaders().getRequestHeaders().getFirst(ORIGIN_HEADER);
        if (origin == null || invalidRequest(origin)) return;

        response.setHeader(ACCESS_CONTROL_ALLOW_ORIGIN, origin);

        if (preflight) addAccessControl(response);
        else if (exposedHeaders != null)
            response.setHeader(ACCESS_CONTROL_EXPOSE_HEADERS, CollectionUtil.join(exposedHeaders));

        response.setHeader(ACCESS_CONTROL_ALLOW_CREDENTIALS, Boolean.toString(auth));

        if (preflight) {
            addAuth(response);
            response.setHeader(ACCESS_CONTROL_MAX_AGE, String.valueOf(DEFAULT_MAX_AGE));
        }
    }

    private boolean invalidRequest(String origin) {
        return !preflight && (allowedOrigins == null ||
                (!allowedOrigins.contains(origin) && !allowedOrigins.contains(ACCESS_CONTROL_ALLOW_ORIGIN_WILDCARD)));
    }

    private void logInfo(String msg, Object... args) { if (logger.isInfoEnabled()) logger.infov(msg, args); }

    private void addAccessControl() {
        builder.header(ACCESS_CONTROL_ALLOW_METHODS,
                allowedMethods != null ? CollectionUtil.join(allowedMethods) : DEFAULT_ALLOW_METHODS);
    }

    private void addAccessControl(HttpResponse r) {
        r.setHeader(ACCESS_CONTROL_ALLOW_METHODS,
                allowedMethods != null ? CollectionUtil.join(allowedMethods) : DEFAULT_ALLOW_METHODS);
    }

    private void addAuth() {
        builder.header(ACCESS_CONTROL_ALLOW_HEADERS,
                auth ? DEFAULT_ALLOW_HEADERS + ", " + AUTHORIZATION_HEADER : DEFAULT_ALLOW_HEADERS);
    }

    private void addAuth(HttpResponse r) {
        r.setHeader(ACCESS_CONTROL_ALLOW_HEADERS,
                auth ? DEFAULT_ALLOW_HEADERS + ", " + AUTHORIZATION_HEADER : DEFAULT_ALLOW_HEADERS);
    }
}