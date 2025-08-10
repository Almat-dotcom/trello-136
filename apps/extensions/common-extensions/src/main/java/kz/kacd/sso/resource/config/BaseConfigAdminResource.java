package kz.kacd.sso.resource.config;

import jakarta.ws.rs.NotAuthorizedException;
import kz.kacd.sso.k8s.K8sConfig;
import kz.kacd.sso.resource.AbstractAdminResource;
import jakarta.ws.rs.InternalServerErrorException;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.jboss.logging.Logger;
import org.keycloak.models.AdminRoles;

public abstract class BaseConfigAdminResource extends AbstractAdminResource {
    private static final Logger log = Logger.getLogger(BaseConfigAdminResource.class);

    protected BaseConfigAdminResource(KeycloakSession session, RealmModel realm) {
        super(session, realm);
    }

    protected <T extends BaseConfigAdminResource> T setupResource(T resource) {
        resource.setup();
        return resource;
    }

    protected void checkPermissions() {
        log.infof("Checking permissions for user: %s", auth.getUser().getUsername());
        boolean hasRealmConfig = auth().hasRealmConfig();
        log.infof("User has realm-config permission: %s", hasRealmConfig);
        if (!hasRealmConfig) {
            throw new NotAuthorizedException("User has no permissions to run configuration!");
        }
    }

    protected void checkConfig() {
        if (!K8sConfig.ENABLED) {
            throw new InternalServerErrorException("Server does not configured to use kubernetes api!");
        }
    }

    private ConfigAdminAuth auth() {
        return new ConfigAdminAuth(auth);
    }

    @Override
    protected final void init() {
        // Nothing to init
    }
}
