package kz.kacd.sso.resource.profile;

import org.keycloak.services.resources.admin.AdminAuth;

public class ProfileAdminAuth {

    public static final String QUERY_PROFILES_ROLE = "query-profiles";
    public static final String UPDATE_LOGIN_OPTIONS_ROLE = "update-login-options";

    private final AdminAuth delegate;

    public ProfileAdminAuth(AdminAuth delegate) {
        this.delegate = delegate;
    }

    public boolean hasQueryProfiles() {
        return delegate.hasAppRole(delegate.getClient(), QUERY_PROFILES_ROLE);
    }

    public boolean hasUpdateProfile() {
        return delegate.hasAppRole(delegate.getClient(), UPDATE_LOGIN_OPTIONS_ROLE);
    }
}
