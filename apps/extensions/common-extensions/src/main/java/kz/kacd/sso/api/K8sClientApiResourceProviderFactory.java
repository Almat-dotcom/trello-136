package kz.kacd.sso.api;

import com.google.auto.service.AutoService;
import org.jboss.logging.Logger;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.services.resource.RealmResourceProvider;
import org.keycloak.services.resource.RealmResourceProviderFactory;

@AutoService(RealmResourceProviderFactory.class)
public class K8sClientApiResourceProviderFactory implements RealmResourceProviderFactory {
    private static final Logger log = Logger.getLogger(K8sClientApiResourceProviderFactory.class);
    public static final String ID = "k8s-client-api";

    @Override
    public String getId() {
        log.infof("K8sClientApiResourceProviderFactory.getId() called, returning: %s", ID);
        return ID;
    }

    @Override
    public RealmResourceProvider create(KeycloakSession session) {
        log.infof("K8sClientApiResourceProviderFactory.create() called with session: %s", session);
        try {
            K8sClientApiResource provider = new K8sClientApiResource(session, session.getContext().getRealm());
            log.infof("K8sClientApiResource created successfully");
            return provider;
        } catch (Exception e) {
            log.error("Error creating K8sClientApiResource", e);
            throw new RuntimeException("Failed to create K8sClientApiResource", e);
        }
    }

    @Override
    public void init(Config.Scope config) {
        log.infof("K8sClientApiResourceProviderFactory.init() called");
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        log.infof("K8sClientApiResourceProviderFactory.postInit() called");
    }

    @Override
    public void close() {
        log.infof("K8sClientApiResourceProviderFactory.close() called");
    }
}



