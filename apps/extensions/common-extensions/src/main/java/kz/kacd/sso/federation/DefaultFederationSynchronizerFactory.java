package kz.kacd.sso.federation;

import com.google.auto.service.AutoService;
import org.keycloak.Config;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.RealmModel;
import org.keycloak.models.utils.KeycloakModelUtils;

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
        KeycloakSessionFactory factory = event.getSession().getKeycloakSessionFactory();
        KeycloakModelUtils.runJobInTransaction(
                factory,
                session -> {
                    RealmModel realm = session.realms().getRealm(event.getRealm().getId());
                    session.getContext().setRealm(event.getRealm());
                    ComponentModel ldap = realm.getComponent(event.getLdap().getId());
                    ComponentModel mapper = realm.getComponent(event.getMapper().getId());
                    if (event.syncToLdap()) {
                        create(session).syncToLdap(realm, ldap, mapper);
                    } else {
                        create(session).syncToKeycloak(realm, ldap, mapper);
                    }
                }
        );
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
