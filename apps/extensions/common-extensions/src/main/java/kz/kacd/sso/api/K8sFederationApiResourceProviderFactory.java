package kz.kacd.sso.api;

import com.google.auto.service.AutoService;
import org.jboss.logging.Logger;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.services.resource.RealmResourceProviderFactory;

@AutoService(RealmResourceProviderFactory.class)
public class K8sFederationApiResourceProviderFactory implements RealmResourceProviderFactory {
    private static final Logger log = Logger.getLogger(K8sFederationApiResourceProviderFactory.class);

    @Override
    public K8sFederationApiResource create(KeycloakSession session) {
        log.infof("K8sFederationApiResourceProviderFactory.create() called");
        return new K8sFederationApiResource(session, session.getContext().getRealm());
    }

    @Override
    public void init(Config.Scope config) {
        log.infof("K8sFederationApiResourceProviderFactory.init() called");
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        log.infof("K8sFederationApiResourceProviderFactory.postInit() called");
    }

    @Override
    public void close() {
        log.infof("K8sFederationApiResourceProviderFactory.close() called");
    }

    @Override
    public String getId() {
        return "k8s-federation-api";
    }
}



