package kz.kacd.sso.resource;

import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.UriInfo;
import kz.kacd.sso.resource.cors.Cors;
import kz.kacd.sso.resource.cors.CorsResource;
import org.jboss.logging.Logger;
import org.keycloak.Config;
import org.keycloak.common.ClientConnection;
import org.keycloak.http.HttpRequest;
import org.keycloak.http.HttpResponse;
import org.keycloak.jose.jws.JWSInput;
import org.keycloak.jose.jws.JWSInputException;
import org.keycloak.models.*;
import org.keycloak.representations.AccessToken;
import org.keycloak.services.managers.*;
import org.keycloak.services.resources.admin.AdminAuth;
import org.keycloak.services.resources.admin.AdminEventBuilder;
import org.keycloak.services.resources.admin.permissions.AdminPermissionEvaluator;
import org.keycloak.services.resources.admin.permissions.AdminPermissions;

public abstract class AbstractAdminResource {

    private static final Logger LOG = Logger.getLogger(AbstractAdminResource.class);

    protected final RealmModel realm;

    @Context
    protected KeycloakSession session;

    protected AdminAuth               auth;
    protected AdminPermissionEvaluator permissions;
    protected AdminEventBuilder       adminEvent;
    protected UserModel               user;
    protected RealmModel              adminRealm;

    protected AbstractAdminResource(RealmModel realm) {
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
        HttpRequest  request  = session.getContext().getHttpRequest();
        HttpResponse response = session.getContext().getHttpResponse();

        Cors.add(request)
                .allowedOrigins(auth.getToken())
                .allowedMethods(CorsResource.METHODS)
                .exposedHeaders("Location")
                .auth()
                .build(response);
    }

    private void setupAuth() {
        HttpRequest request = session.getContext().getHttpRequest();
        HttpHeaders headers = request.getHttpHeaders();
        String      tokenString = AppAuthManager.extractAuthorizationHeaderToken(headers);
        if (tokenString == null) throw new NotAuthorizedException("Bearer");

        AccessToken token;
        try {
            token = new JWSInput(tokenString).readJsonContent(AccessToken.class);
        } catch (JWSInputException e) {
            throw new NotAuthorizedException("Bearer token format error");
        }

        String       realmName = token.getIssuer().substring(token.getIssuer().lastIndexOf('/') + 1);
        RealmManager realmMgr  = new RealmManager(session);
        adminRealm             = realmMgr.getRealmByName(realmName);
        if (adminRealm == null) throw new NotAuthorizedException("Unknown realm in token");

        session.getContext().setRealm(adminRealm);
        AuthenticationManager.AuthResult authResult = authenticateBearerToken(
                tokenString,
                session,
                adminRealm,
                session.getContext().getUri(),
                session.getContext().getConnection(),
                headers
        );
        session.getContext().setRealm(realm);
        if (authResult == null) throw new NotAuthorizedException("Bearer");

        ClientModel client =
                adminRealm.getName().equals(Config.getAdminRealm())
                        ? realm.getMasterAdminClient()
                        : realm.getClientByClientId(realmMgr.getRealmAdminClientId(realm));
        if (client == null) throw new NotFoundException("Could not find client for authorization");

        user = authResult.getUser();
        auth = new AdminAuth(realm, token, user, client);
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

    private void setupEvents() {
        adminEvent = new AdminEventBuilder(realm, auth, session,
                session.getContext().getConnection()).realm(realm);
    }

    private void setupPermissions() {
        permissions = AdminPermissions.evaluator(session, realm, adminRealm, user);
    }
}