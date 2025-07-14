package kz.kacd.sso.external.resource;

import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.UriInfo;
import kz.kacd.sso.external.resource.common.ExternalAdminAuth;
import kz.kacd.sso.external.resource.cors.Cors;
import kz.kacd.sso.external.resource.cors.CorsResource;
import org.jboss.logging.Logger;
import org.keycloak.http.HttpRequest;
import org.keycloak.http.HttpResponse;
import org.keycloak.Config;
import org.keycloak.common.ClientConnection;
import org.keycloak.jose.jws.JWSInput;
import org.keycloak.jose.jws.JWSInputException;
import org.keycloak.models.ClientModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.representations.AccessToken;
import org.keycloak.services.managers.AppAuthManager;
import org.keycloak.services.managers.AuthenticationManager;
import org.keycloak.services.managers.RealmManager;
import org.keycloak.services.resources.admin.AdminEventBuilder;
import org.keycloak.services.resources.admin.permissions.AdminPermissionEvaluator;
import org.keycloak.services.resources.admin.permissions.AdminPermissions;

/**
 * Base class for admin resources.
 * </>
 * Initializes request context and authentication.
 */
public abstract class AbstractAdminResource {
    private static final Logger log = Logger.getLogger(AbstractAdminResource.class);
    protected final RealmModel realm;
    protected final KeycloakSession session;
    protected ExternalAdminAuth auth;
    protected AdminPermissionEvaluator permissions;
    protected AdminEventBuilder adminEvent;
    protected UserModel user;
    protected RealmModel adminRealm;

    protected AbstractAdminResource(KeycloakSession session, RealmModel realm) {
        this.session = session;
        this.realm = realm;
    }

    protected abstract void init();

    public final void setup() {
        setupAuth();
        setupEvents();
        setupPermissions();
        setupCors();
        init();
    }

    private void setupCors() {
        HttpRequest request = session.getContext().getHttpRequest();
        HttpResponse response = session.getContext().getHttpResponse();

        Cors.add(request)
                .allowedOrigins(auth.getToken())
                .allowedMethods(CorsResource.METHODS)
                .exposedHeaders("Location")
                .auth()
                .build(response);
    }

    private void setupAuth() {
        HttpRequest req = session.getContext().getHttpRequest();
        HttpHeaders headers = req.getHttpHeaders();
        String tokenString = AppAuthManager.extractAuthorizationHeaderToken(headers);

        if (tokenString == null) {
            throw new NotAuthorizedException("Bearer");
        }

        AccessToken token;
        try {
            token = new JWSInput(tokenString).readJsonContent(AccessToken.class);
        } catch (JWSInputException e) {
            throw new NotAuthorizedException("Bearer token format error");
        }

        String realmName = token.getIssuer().substring(token.getIssuer().lastIndexOf('/') + 1);
        RealmManager realmManager = new RealmManager(session);
        adminRealm = realmManager.getRealmByName(realmName);

        if (adminRealm == null) {
            throw new NotAuthorizedException("Unknown realm in token");
        }


        log.debugf(
                "Realm from resource provider is %s. Realm from token is %s",
                this.realm.getName(),
                adminRealm.getName()
        );
        session.getContext().setRealm(adminRealm);
        AuthenticationManager.AuthResult authResult = authenticateBearerToken(
                tokenString,
                session,
                adminRealm,
                session.getContext().getUri(),
                session.getContext().getConnection(),
                headers
        );
        if (authResult == null) {
            throw new NotAuthorizedException("Bearer");
        }
        session.getContext().setRealm(this.realm);

        ClientModel client =
                adminRealm.getName().equals(Config.getAdminRealm())
                        ? this.realm.getMasterAdminClient()
                        : this.realm.getClientByClientId(realmManager.getRealmAdminClientId(this.realm));

        if (client == null) {
            throw new NotFoundException("Could not find client for authorization");
        }

        user = authResult.getUser();
        auth = new ExternalAdminAuth(realm, token, user, client);
    }

    private void setupEvents() {
        adminEvent =
                new AdminEventBuilder(this.realm, auth, session, session.getContext().getConnection())
                        .realm(realm);
    }

    private void setupPermissions() {
        permissions = AdminPermissions.evaluator(session, realm, adminRealm, user);
    }

    private AuthenticationManager.AuthResult authenticateBearerToken(
            String tokenString,
            KeycloakSession session,
            RealmModel realm,
            UriInfo uriInfo,
            ClientConnection connection,
            HttpHeaders headers) {
        return new AppAuthManager.BearerTokenAuthenticator(session)
                .setRealm(realm)
                .setUriInfo(uriInfo)
                .setTokenString(tokenString)
                .setConnection(connection)
                .setHeaders(headers)
                .authenticate();
    }
}
