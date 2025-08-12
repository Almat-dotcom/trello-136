package kz.kacd.sso;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.NotFoundException;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserProvider;

public abstract class BaseAdminResource extends AbstractAdminResource {

    protected UserProvider users;

    protected BaseAdminResource(KeycloakSession session,RealmModel realm) {
        super(  session,realm);
    }

    protected <T extends BaseAdminResource> T setupResource(T resource) {
        ResteasyProviderFactory.getInstance().injectProperties(resource);
        resource.setup();
        return resource;
    }


    protected NotFoundException organizationNotFound(String id) {
        return new NotFoundException(String.format("Organization %s not found!", id));
    }

    protected BadRequestException userNotFound(String userId) {
        return new BadRequestException(String.format("User %s  not found!", userId));
    }

    protected BadRequestException positionNotFound(String name) {
        return new BadRequestException(String.format("Position %s not found!", name));
    }

    @Override
    protected final void init() {
        this.users = session.users();
    }
}
