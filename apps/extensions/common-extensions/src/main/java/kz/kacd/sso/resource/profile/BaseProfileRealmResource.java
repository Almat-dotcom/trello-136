package kz.kacd.sso.resource.profile;

import jakarta.ws.rs.ForbiddenException;
import kz.kacd.sso.resource.AbstractAdminResource;
import kz.kacd.sso.resource.config.BaseConfigAdminResource;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;

public class BaseProfileRealmResource extends AbstractAdminResource {

    protected BaseProfileRealmResource(KeycloakSession session, RealmModel realm) {
        super(session, realm);
    }

    protected <T extends BaseConfigAdminResource> T setupResource(T resource) {
        resource.setup();
        return resource;
    }

    protected void hasReadPermission() {
        if (!auth().hasQueryProfiles()) {
            throw new ForbiddenException("You have not right permissions!");
        }
    }

    protected void hasUpdatePermission() {
        if (!auth().hasUpdateProfile()) {
            throw new ForbiddenException("You have not right permissions!");
        }
    }

    private ProfileAdminAuth auth() {
        return new ProfileAdminAuth(auth);
    }

    @Override
    protected void init() {
        // Nothing to init
    }
}
