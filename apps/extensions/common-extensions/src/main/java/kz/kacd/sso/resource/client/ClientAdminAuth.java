package kz.kacd.sso.resource.client;

import org.keycloak.services.resources.admin.AdminAuth;

public class ClientAdminAuth {

    public static final String QUERY_CLIENT_ROLES = "query-client-roles";
    private static final String QUERY_CLIENTS = "query-clients";

    private final AdminAuth auth;

    public ClientAdminAuth(AdminAuth auth) {
        this.auth = auth;
    }

    public boolean hasClientRolesReadPermission() {
        return hasClientReadPermission() && auth.hasAppRole(auth.getClient(), QUERY_CLIENT_ROLES);
    }

    public boolean hasClientReadPermission() {
        return auth.hasAppRole(auth.getClient(), QUERY_CLIENTS);
    }
}
