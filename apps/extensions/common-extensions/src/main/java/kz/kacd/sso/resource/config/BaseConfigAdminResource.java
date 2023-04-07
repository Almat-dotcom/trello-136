package kz.kacd.sso.resource.config;

import kz.kacd.sso.k8s.K8sConfig;
import kz.kacd.sso.resource.AbstractAdminResource;
import org.jboss.resteasy.spi.InternalServerErrorException;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.keycloak.models.RealmModel;

import javax.ws.rs.NotAuthorizedException;

public abstract class BaseConfigAdminResource extends AbstractAdminResource {

    protected BaseConfigAdminResource(RealmModel realm) {
        super(realm);
    }

    protected <T extends BaseConfigAdminResource> T setupResource(T resource) {
        ResteasyProviderFactory.getInstance().injectProperties(resource);
        resource.setup();
        return resource;
    }

    protected void checkPermissions() {
        if (!auth().hasRealmConfig()) {
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
