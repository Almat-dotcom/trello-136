package kz.kacd.sso.external.resource;

import kz.kacd.sso.external.resource.cors.CorsResource;
import org.jboss.logging.Logger;
import org.keycloak.models.KeycloakSession;
import org.keycloak.services.resource.RealmResourceProvider;
import org.keycloak.http.HttpRequest;

public abstract class BaseRealmResourceProvider implements RealmResourceProvider {
    private static final Logger log = Logger.getLogger(BaseRealmResourceProvider.class);

    protected final KeycloakSession session;

    protected BaseRealmResourceProvider(KeycloakSession session) {
        this.session = session;
    }

    @Override
    public void close() {
        // Nothing to close
    }

    protected abstract Object getRealmResource();

    @Override
    public Object getResource() {
        HttpRequest request = session.getContext().getHttpRequest();
        log.debugf("request method %s", request.getHttpMethod());
        if ("OPTIONS".equals(request.getHttpMethod())) {
            return new CorsResource(request);
        } else {
            return getRealmResource();
        }
    }
}
