package kz.kacd.sso.external.model.jpa;

import kz.kacd.sso.external.model.PositionModel;
import kz.kacd.sso.external.model.jpa.entity.OrganizationMemberEntity;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.jpa.JpaModel;

public class PositionAdapter implements PositionModel, JpaModel<OrganizationMemberEntity> {

    private final KeycloakSession keycloakSession;
    private final OrganizationMemberEntity entity;
    private final RealmModel realm;

    private UserModel user;

    public PositionAdapter(
            KeycloakSession keycloakSession,
            OrganizationMemberEntity entity,
            RealmModel realm
    ) {
        this.keycloakSession = keycloakSession;
        this.entity = entity;
        this.realm = realm;
    }

    @Override
    public String getId() {
        return entity.getId();
    }

    @Override
    public String getName() {
        return entity.getPosition().getName();
    }

    @Override
    public String getDescription() {
        return entity.getPosition().getDescription();
    }

    @Override
    public UserModel getUser() {
        if (user != null) {
            return user;
        }

        user = keycloakSession.users().getUserById(realm, entity.getUserId());
        return user;
    }

    @Override
    public boolean confirmed() {
        return entity.getConfirmed();
    }

    @Override
    public OrganizationMemberEntity getEntity() {
        return entity;
    }
}
