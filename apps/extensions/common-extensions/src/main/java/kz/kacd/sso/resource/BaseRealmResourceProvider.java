package kz.kacd.sso.resource;

import kz.kacd.sso.resource.cors.CorsResource;
import org.jboss.logging.Logger;
import org.keycloak.http.HttpRequest;          // новый импорт
import org.keycloak.models.KeycloakContext;
import org.keycloak.models.KeycloakSession;
import org.keycloak.services.resource.RealmResourceProvider;

public abstract class BaseRealmResourceProvider implements RealmResourceProvider {

    private static final Logger LOG = Logger.getLogger(BaseRealmResourceProvider.class);
    protected final KeycloakSession session;

    protected BaseRealmResourceProvider(KeycloakSession session) {
        this.session = session;
    }

    @Override
    public void close() { /* nothing */ }

    protected abstract Object getRealmResource();

    @Override
    public Object getResource() {
        KeycloakContext kcCtx   = session.getContext();
        HttpRequest      request = kcCtx.getHttpRequest();   // новый способ

        if (request == null) {               // крайне редко, но на всякий случай
            LOG.warn("ALMAOOOO HttpRequest is null - falling back to realm resource");
            return getRealmResource();
        }

        LOG.debugf("Request method %s", request.getHttpMethod());

        return getRealmResource();
    }
}
