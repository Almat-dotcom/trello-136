package kz.kacd.sso.federation;

import com.google.auto.service.AutoService;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

@AutoService(FederationSynchronizerFactory.class)
public class DefaultFederationSynchronizerFactory implements FederationSynchronizerFactory {
    private static final String PROVIDER_ID = "default-federation-synchronizer";

    @Override
    public FederationSynchronizer create(KeycloakSession session) {
        return new DefaultFederationSynchronizer(session);
    }

    @Override
    public void init(Config.Scope config) {
        // Nothing to init
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        factory.register(event -> {
            if (event instanceof FederationConfigurer.GroupsOrRolesMapperConfigured) {
                sync((FederationConfigurer.GroupsOrRolesMapperConfigured) event);
            }
        });
    }

    private void sync(FederationConfigurer.GroupsOrRolesMapperConfigured event) {
        KeycloakSession session = event.getSession();
        if (event.syncToLdap()) {
            create(session).syncToLdap(event.getRealm(), event.getLdap(), event.getMapper());
        } else {
            create(session).syncToKeycloak(event.getRealm(), event.getLdap(), event.getMapper());
        }
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
