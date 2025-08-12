package kz.kacd.sso;

import jakarta.ws.rs.NotAuthorizedException;
import org.keycloak.models.ClientModel;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.representations.AccessToken;
import org.keycloak.services.resources.admin.AdminAuth;

/**
 * Authorization utils for organizations admin REST API.
 */
public class ExternalAdminAuth extends AdminAuth {

    public static final String ORGANIZATION_CREATE_ROLE = "create-organizations";
    public static final String ORGANIZATION_VIEW_ROLE = "view-organizations";
    public static final String ORGANIZATION_MANAGE_ROLE = "manage-organizations";
    public static final String MANAGE_USERS_ROLE = "manage-users";

    public ExternalAdminAuth(RealmModel realm, AccessToken token, UserModel user, ClientModel client) {
        super(realm, token, user, client);
    }

    public void requireCreateOrg() {
        if (!hasCreateOrg()) {
            throw new NotAuthorizedException(ORGANIZATION_CREATE_ROLE);
        }
    }

    public boolean hasCreateOrg() {
        return hasAppRole(getClient(), ORGANIZATION_CREATE_ROLE);
    }

    public void requireViewOrgs() {
        if (!hasViewOrgs()) {
            throw new NotAuthorizedException(ORGANIZATION_VIEW_ROLE);
        }
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

    public void requireManageUsers() {
        if (!hasManageUsers()) {
            throw new NotAuthorizedException(MANAGE_USERS_ROLE);
        }
    }

    public boolean hasManageUsers() {
        return hasAppRole(getClient(), MANAGE_USERS_ROLE);
    }
}
