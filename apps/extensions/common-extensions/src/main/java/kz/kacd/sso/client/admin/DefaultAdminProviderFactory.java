package kz.kacd.sso.client.admin;

import com.google.auto.service.AutoService;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.RealmModel;

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
        factory.register(event -> {
            if (event instanceof RealmModel.RealmPostCreateEvent) {
                RealmModel.RealmPostCreateEvent creationEvent = (RealmModel.RealmPostCreateEvent) event;
                AdminClientProvider admins = creationEvent.getKeycloakSession().getProvider(AdminClientProvider.class);
                admins.configureAdminClient(creationEvent.getCreatedRealm());
            }
        });
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
