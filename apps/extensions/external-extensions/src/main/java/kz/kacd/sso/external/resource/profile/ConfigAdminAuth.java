package kz.kacd.sso.external.resource.profile;

import org.keycloak.services.resources.admin.AdminAuth;

/**
 * Authorization utils for organizations admin REST API.
 */
public class ConfigAdminAuth {

    public static final String REALM_CONFIG_ROLE = "realm-config";

    private final AdminAuth delegate;

    public ConfigAdminAuth(AdminAuth delegate) {
        this.delegate = delegate;
    }

    public boolean hasRealmConfig() {
        return delegate.hasAppRole(delegate.getClient(), REALM_CONFIG_ROLE);
    }
}
