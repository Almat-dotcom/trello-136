package kz.kacd.sso.external.model.jpa;

import com.google.auto.service.AutoService;
import jakarta.persistence.EntityManager;
import kz.kacd.sso.external.model.OrganizationProvider;
import kz.kacd.sso.external.model.OrganizationProviderFactory;
import org.keycloak.Config;
import org.keycloak.connections.jpa.JpaConnectionProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

@AutoService(value = {OrganizationProviderFactory.class})
public class JpaOrganizationProviderFactory implements OrganizationProviderFactory {

    public static final String PROVIDER_ID = "jpa-organization";

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public OrganizationProvider create(KeycloakSession session) {
        EntityManager em = session.getProvider(JpaConnectionProvider.class).getEntityManager();
        AdaptersFactory factory = new AdaptersFactory();
        return new JpaOrganizationProvider(factory, session, em);
    }

    @Override
    public void init(Config.Scope config) {
        // No configuration for this bean
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        // No need to perform some actions after init
    }

    @Override
    public void close() {
        // Nothing to close
    }
}
