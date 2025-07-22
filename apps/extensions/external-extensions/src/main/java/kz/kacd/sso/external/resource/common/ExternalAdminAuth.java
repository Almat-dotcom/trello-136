package kz.kacd.sso.external.resource.common;

import jakarta.ws.rs.NotAuthorizedException;
import org.keycloak.models.ClientModel;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.representations.AccessToken;
import org.keycloak.services.resources.admin.AdminAuth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Authorization utils for organizations admin REST API.
 */
public class ExternalAdminAuth extends AdminAuth {

    public static final String ORGANIZATION_CREATE_ROLE = "create-organizations";
    public static final String ORGANIZATION_VIEW_ROLE = "view-organizations";
    public static final String ORGANIZATION_MANAGE_ROLE = "manage-organizations";
    public static final String MANAGE_USERS_ROLE = "manage-users";
    public static final String QUERY_CLIENT_ROLES = "query-client-roles";
    private static final String QUERY_CLIENTS = "query-clients";
    public static final String QUERY_PROFILES_ROLE = "query-profiles";
    public static final String UPDATE_LOGIN_OPTIONS_ROLE = "update-login-options";
    private static final Logger log = LoggerFactory.getLogger(ExternalAdminAuth.class);

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

    public boolean hasClientRolesReadPermission() {
        return hasClientReadPermission() && hasAppRole(getClient(), QUERY_CLIENT_ROLES);
    }

    public boolean hasClientReadPermission() {
        return hasAppRole(getClient(), QUERY_CLIENTS);
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

    public boolean hasQueryProfiles() {
        return hasAppRole(getClient(), QUERY_PROFILES_ROLE);
    }

    public boolean hasUpdateProfile() {
        return hasAppRole(getClient(), UPDATE_LOGIN_OPTIONS_ROLE);
    }
}
