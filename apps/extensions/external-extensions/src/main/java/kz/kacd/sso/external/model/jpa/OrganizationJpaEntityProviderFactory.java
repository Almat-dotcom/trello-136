package kz.kacd.sso.external.model.jpa;

import com.google.auto.service.AutoService;
import org.keycloak.Config;
import org.keycloak.connections.jpa.entityprovider.JpaEntityProvider;
import org.keycloak.connections.jpa.entityprovider.JpaEntityProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

@AutoService(JpaEntityProviderFactory.class)
public class OrganizationJpaEntityProviderFactory implements JpaEntityProviderFactory {
    public static final String PROVIDER_ID = "organization-entity-provider";

    @Override
    public JpaEntityProvider create(KeycloakSession session) {
        return new OrganizationJpaEntityProvider();
    }

    @Override
    public void init(Config.Scope config) {
        // No configuration in this provider
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        // No need to perform actions after init
    }

    @Override
    public void close() {
        // Nothing to close
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }
}
