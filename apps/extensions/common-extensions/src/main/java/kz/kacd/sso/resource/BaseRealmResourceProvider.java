package kz.kacd.sso.resource;

import kz.kacd.sso.resource.cors.CorsResource;
import org.jboss.logging.Logger;
import org.jboss.resteasy.spi.HttpRequest;
import org.keycloak.models.KeycloakSession;
import org.keycloak.services.resource.RealmResourceProvider;

public abstract class BaseRealmResourceProvider implements RealmResourceProvider {

    private static final Logger log = Logger.getLogger(BaseRealmResourceProvider.class);
    protected final KeycloakSession session;

    protected BaseRealmResourceProvider(KeycloakSession session) {
        this.session = session;
    }

    @Override
    public void close() { /* nothing */ }

    protected abstract Object getRealmResource();

    @Override
    public Object getResource() {
        HttpRequest request = session.getContext().getContextObject(HttpRequest.class);
        log.debugf("request method %s", request.getHttpMethod());
        if ("OPTIONS".equals(request.getHttpMethod())) {
            return new CorsResource(request);
        } else {
            return getRealmResource();
        }
    }
}
