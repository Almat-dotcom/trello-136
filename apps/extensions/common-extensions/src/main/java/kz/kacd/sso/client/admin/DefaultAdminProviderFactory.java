package kz.kacd.sso.client.admin;

import com.google.auto.service.AutoService;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

@AutoService(AdminClientProviderFactory.class)
public class DefaultAdminProviderFactory implements AdminClientProviderFactory {
    private static final String PROVIDER_ID = "default-admin-client-provider";

    @Override
    public AdminClientProvider create(KeycloakSession session) {
        return new DefaultAdminProvider(session);
    }

    @Override
    public void init(Config.Scope config) {
        // Nothing to init
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        // Nothing to do after init
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
