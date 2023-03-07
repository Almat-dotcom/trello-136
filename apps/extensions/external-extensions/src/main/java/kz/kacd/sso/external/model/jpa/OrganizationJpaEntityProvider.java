package kz.kacd.sso.external.model.jpa;

import kz.kacd.sso.external.model.jpa.entity.OrganizationEntity;
import kz.kacd.sso.external.model.jpa.entity.OrganizationMemberEntity;
import kz.kacd.sso.external.model.jpa.entity.PositionEntity;
import org.keycloak.connections.jpa.entityprovider.JpaEntityProvider;

import java.util.ArrayList;
import java.util.List;

public class OrganizationJpaEntityProvider implements JpaEntityProvider {

    @Override
    public List<Class<?>> getEntities() {
        List<Class<?>> result = new ArrayList<>();
        result.add(OrganizationEntity.class);
        result.add(PositionEntity.class);
        result.add(OrganizationMemberEntity.class);
        return result;
    }

    @Override
    public String getChangelogLocation() {
        return "META-INF/external-changelog-master.xml";
    }

    @Override
    public String getFactoryId() {
        return "sample";
    }

    @Override
    public void close() {
        // Nothing to close
    }
}
