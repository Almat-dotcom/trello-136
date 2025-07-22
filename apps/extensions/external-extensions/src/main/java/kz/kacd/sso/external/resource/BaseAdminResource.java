package kz.kacd.sso.external.resource;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.NotFoundException;
import kz.kacd.sso.external.model.OrganizationProvider;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserProvider;

public abstract class BaseAdminResource extends AbstractAdminResource {

    protected OrganizationProvider orgs;
    protected UserProvider users;

    public BaseAdminResource(KeycloakSession session, RealmModel realm) {
        super(session, realm);
    }

    protected <T extends BaseAdminResource> T setupResource(T resource) {
        ResteasyProviderFactory.getInstance().injectProperties(resource);
        resource.setup();
        return resource;
    }

    protected void checkViewPermissions() {
        if (!auth.hasViewOrgs()) {
            throw new NotAuthorizedException("User has no permissions to view organizations!");
        }
    }

    protected void hasReadPermissions() {
        if (!auth.hasClientRolesReadPermission()) {
            throw new ForbiddenException("You have no permissions");
        }
    }

    protected void hasReadPermission() {
        if (!auth.hasQueryProfiles()) {
            throw new ForbiddenException("You have not right permissions!");
        }
    }

    protected void hasUpdatePermission() {
        if (!auth.hasUpdateProfile()) {
            throw new ForbiddenException("You have not right permissions!");
        }
    }

    protected void checkEditPermissions() {
        if (!(auth.hasManageOrgs() || auth.hasCreateOrg())) {
            throw new NotAuthorizedException("User has no permissions to manage organizations!");
        }
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
        this.orgs = session.getProvider(OrganizationProvider.class);
        this.users = session.users();
    }
}
