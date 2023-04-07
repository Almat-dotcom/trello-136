package kz.kacd.sso.resource.profile;

import kz.kacd.sso.resource.AbstractAdminResource;
import kz.kacd.sso.resource.config.BaseConfigAdminResource;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.keycloak.models.RealmModel;

import javax.ws.rs.ForbiddenException;

public class BaseProfileRealmResource extends AbstractAdminResource {

    protected BaseProfileRealmResource(RealmModel realm) {
        super(realm);
    }

    protected <T extends BaseConfigAdminResource> T setupResource(T resource) {
        ResteasyProviderFactory.getInstance().injectProperties(resource);
        resource.setup();
        return resource;
    }

    protected void hasReadPermission() {
        if (!auth().hasQueryProfiles()) {
            throw new ForbiddenException("You have not right permissions!");
        }
    }

    protected void hasUpdatePermission() {
        if (!auth().hasUpdateProfile()) {
            throw new ForbiddenException("You have not right permissions!");
        }
    }

    private ProfileAdminAuth auth() {
        return new ProfileAdminAuth(auth);
    }

    @Override
    protected void init() {
        // Nothing to init
    }
}
