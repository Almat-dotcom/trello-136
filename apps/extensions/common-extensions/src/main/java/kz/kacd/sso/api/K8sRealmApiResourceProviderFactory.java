package kz.kacd.sso.api;

import com.google.auto.service.AutoService;
import org.jboss.logging.Logger;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.services.resource.RealmResourceProviderFactory;

@AutoService(RealmResourceProviderFactory.class)
public class K8sRealmApiResourceProviderFactory implements RealmResourceProviderFactory {
    private static final Logger log = Logger.getLogger(K8sRealmApiResourceProviderFactory.class);

    @Override
    public K8sRealmApiResource create(KeycloakSession session) {
        log.infof("K8sRealmApiResourceProviderFactory.create() called");
        return new K8sRealmApiResource(session, session.getContext().getRealm());
    }

    @Override
    public void init(Config.Scope config) {
        log.infof("K8sRealmApiResourceProviderFactory.init() called");
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        log.infof("K8sRealmApiResourceProviderFactory.postInit() called");
    }

    @Override
    public void close() {
        log.infof("K8sRealmApiResourceProviderFactory.close() called");
    }

    @Override
    public String getId() {
        return "k8s-realm-api";
    }
}



