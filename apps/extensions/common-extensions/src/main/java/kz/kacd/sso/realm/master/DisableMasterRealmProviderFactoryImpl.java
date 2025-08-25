package kz.kacd.sso.realm.master;

import com.google.auto.service.AutoService;
import org.jboss.logging.Logger;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.RealmModel;
import org.keycloak.models.utils.PostMigrationEvent;

@AutoService(DisableMasterRealmProviderFactory.class)
public class DisableMasterRealmProviderFactoryImpl implements DisableMasterRealmProviderFactory {
    private static final Logger log = Logger.getLogger(DisableMasterRealmProviderFactoryImpl.class);

    private static final String PROVIDER_ID = "default-disable-master-realm-provider";
    private static final String MASTER_REALM_ENABLED = "MASTER_REALM_ENABLED";

    @Override
    public DisableMasterRealmProvider create(KeycloakSession session) {
        return new DisableMasterRealmProviderImpl();
    }

    @Override
    public void init(Config.Scope config) {
        // Nothing to init
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        factory.register(event -> {
            if (event instanceof PostMigrationEvent) {
                checkMasterRealm((PostMigrationEvent) event);
            }
        });
    }

    private void checkMasterRealm(PostMigrationEvent event) {
        KeycloakSession session = event.getFactory().create();
        session.getTransactionManager().begin();
        try {
            RealmModel master = session.realms().getRealmByName("master");
            boolean enabled = getMasterEnabled();
            log.info("Making master realm as " + (enabled ? "enabled" : "disabled") + " ...");
            master.setEnabled(enabled);
            session.getTransactionManager().commit();
        } catch (Exception e) {
            log.error("Error on manipulating with master realm!", e);
            session.getTransactionManager().rollback();
        } finally {
            session.close();
        }
    }

    private boolean getMasterEnabled() {
        String env = System.getenv(MASTER_REALM_ENABLED);
        return env != null && env.equals("true");
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
