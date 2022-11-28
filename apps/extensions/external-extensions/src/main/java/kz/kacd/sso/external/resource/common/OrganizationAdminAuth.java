package kz.kacd.sso.external.resource.common;

import org.keycloak.models.ClientModel;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.representations.AccessToken;
import org.keycloak.services.resources.admin.AdminAuth;

import javax.ws.rs.NotAuthorizedException;

/**
 * Authorization utils for organizations admin REST API.
 */
public class OrganizationAdminAuth extends AdminAuth {

    public static final String ORGANIZATION_CREATE_ROLE = "create-organizations";
    public static final String ORGANIZATION_VIEW_ROLE = "view-organizations";
    public static final String ORGANIZATION_MANAGE_ROLE = "manage-organizations";

    public OrganizationAdminAuth(RealmModel realm, AccessToken token, UserModel user, ClientModel client) {
        super(realm, token, user, client);
    }

    public void requireCreateOrg() {
        if (!hasAppRole(getClient(), ORGANIZATION_CREATE_ROLE))
            throw new NotAuthorizedException(ORGANIZATION_CREATE_ROLE);
    }

    public boolean hasCreateOrg() {
        return hasAppRole(getClient(), ORGANIZATION_CREATE_ROLE);
    }

    public void requireViewOrgs() {
        if (!hasAppRole(getClient(), ORGANIZATION_VIEW_ROLE))
            throw new NotAuthorizedException(ORGANIZATION_VIEW_ROLE);
    }

    public boolean hasViewOrgs() {
        return hasAppRole(getClient(), ORGANIZATION_VIEW_ROLE);
    }

    public void requireManageOrgs() {
        if (!hasAppRole(getClient(), ORGANIZATION_MANAGE_ROLE))
            throw new NotAuthorizedException(ORGANIZATION_MANAGE_ROLE);
    }

    public boolean hasManageOrgs() {
        return hasAppRole(getClient(), ORGANIZATION_MANAGE_ROLE);
    }
}
