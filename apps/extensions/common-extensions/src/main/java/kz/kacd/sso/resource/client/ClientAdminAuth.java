package kz.kacd.sso.resource.client;

import org.jboss.logging.Logger;
import org.keycloak.services.resources.admin.AdminAuth;

public class ClientAdminAuth {
    private static final Logger log = Logger.getLogger(ClientAdminAuth.class);

    public static final String QUERY_CLIENT_ROLES = "query-client-roles";
    private static final String QUERY_CLIENTS = "query-clients";

    private final AdminAuth auth;

    public ClientAdminAuth(AdminAuth auth) {
        log.info("Client Admin Auth");
        this.auth = auth;
    }

    public boolean hasClientRolesReadPermission() {
        return hasClientReadPermission() && auth.hasAppRole(auth.getClient(), QUERY_CLIENT_ROLES);
    }

    public boolean hasClientReadPermission() {
        return auth.hasAppRole(auth.getClient(), QUERY_CLIENTS);
    }
}
