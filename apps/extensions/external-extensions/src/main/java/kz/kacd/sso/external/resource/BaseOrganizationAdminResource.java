package kz.kacd.sso.external.resource;

import kz.kacd.sso.external.model.OrganizationProvider;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.keycloak.models.RealmModel;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;

public abstract class BaseOrganizationAdminResource extends AbstractAdminResource {

    protected OrganizationProvider orgs;

    protected BaseOrganizationAdminResource(RealmModel realm) {
        super(realm);
    }

    protected <T extends BaseOrganizationAdminResource> T setupResource(T resource) {
        ResteasyProviderFactory.getInstance().injectProperties(resource);
        resource.setup();
        return resource;
    }

    protected void checkViewPermissions() {
        if (!auth.hasViewOrgs()) {
            throw new NotAuthorizedException("User has no permissions to view organizations!");
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
    }
}
