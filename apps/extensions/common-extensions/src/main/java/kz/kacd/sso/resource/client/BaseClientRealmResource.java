package kz.kacd.sso.resource.client;

import jakarta.ws.rs.ForbiddenException;
import kz.kacd.sso.resource.AbstractAdminResource;
import org.keycloak.models.RealmModel;

public class BaseClientRealmResource extends AbstractAdminResource {

    protected BaseClientRealmResource(RealmModel realm) {
        super(realm);
    }

    protected void hasReadPermissions() {
        if (auth().hasClientRolesReadPermission()) {
            throw new ForbiddenException("You have no permissions");
        }
    }

    protected ClientAdminAuth auth() {
        return new ClientAdminAuth(auth);
    }

    @Override
    protected void init() {
        // Nothing to init
    }
}
