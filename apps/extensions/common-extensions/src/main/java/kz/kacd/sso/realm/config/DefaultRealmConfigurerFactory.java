package kz.kacd.sso.realm.config;

import com.google.auto.service.AutoService;
import kz.kacd.sso.file.FileImportResourceProvider;
import org.jboss.logging.Logger;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

@AutoService(KeycloakRealmConfigurerFactory.class)
public class DefaultRealmConfigurerFactory implements KeycloakRealmConfigurerFactory {
    private static final Logger log = Logger.getLogger(DefaultRealmConfigurerFactory.class);

    private static final String PROVIDER_ID = "default-realm-configurer";

    @Override
    public KeycloakRealmConfigurer create(KeycloakSession session) {
        return new DefaultRealmConfigurer(session);
    }

    @Override
    public void init(Config.Scope config) {
        // Nothing to config
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        factory.register(event -> {
            if (event instanceof FileImportResourceProvider.RealmImportResourceFound) {
                applyRealm((FileImportResourceProvider.RealmImportResourceFound) event);
            }
        });
    }

    private void applyRealm(FileImportResourceProvider.RealmImportResourceFound event) {
        log.infof("Found import resource realm %s", event.getResource().getMetadata().getName());
        KeycloakSessionFactory factory = event.getSession();
        KeycloakSession session = factory.create();
        try {
            session.getTransactionManager().begin();
            create(session).configure(event.getResource());
            session.getTransactionManager().commit();
        } catch (Exception e) {
            log.error("", e);
            session.getTransactionManager().rollback();
        } finally {
            session.close();
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
