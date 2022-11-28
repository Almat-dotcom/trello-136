package kz.kacd.sso.external.model.jpa;

import kz.kacd.sso.external.model.OrganizationModel;
import kz.kacd.sso.external.model.jpa.entity.OrganizationEntity;
import kz.kacd.sso.external.model.jpa.entity.OrganizationMemberEntity;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;

import javax.persistence.EntityManager;

public class AdaptersFactory {

    public OrganizationModel create(
            KeycloakSession keycloakSession,
            OrganizationEntity entity,
            AdaptersFactory adaptersFactory,
            EntityManager em,
            RealmModel realm
    ) {
        return new OrganizationAdapter(keycloakSession, entity, adaptersFactory, em, realm);
    }

    public PositionAdapter create(
            KeycloakSession keycloakSession,
            OrganizationMemberEntity entity,
            RealmModel realm
    ) {
        return new PositionAdapter(keycloakSession, entity, realm);
    }

}
