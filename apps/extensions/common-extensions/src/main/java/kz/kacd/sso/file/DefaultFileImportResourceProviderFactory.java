package kz.kacd.sso.file;

import com.google.auto.service.AutoService;
import org.jboss.logging.Logger;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.utils.PostMigrationEvent;

@AutoService(FileImportResourceProviderFactory.class)
public class DefaultFileImportResourceProviderFactory implements FileImportResourceProviderFactory {
    private static final Logger log = Logger.getLogger(DefaultFileImportResourceProviderFactory.class);

    private static final String PROVIDER_ID = "default-file-import-provider";

    @Override
    public FileImportResourceProvider create(KeycloakSession session) {
        return new DefaultFileImportResourceProvider(session);
    }

    @Override
    public void init(Config.Scope config) {
        // Nothing to init
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        factory.register(event -> {
            if (event instanceof PostMigrationEvent) {
                KeycloakSessionFactory sessionFactory = ((PostMigrationEvent) event).getFactory();
                KeycloakSession session = sessionFactory.create();
                try {
                    session.getTransactionManager().begin();
                    create(session).findRealmResources();
                    session.getTransactionManager().commit();
                } catch (Exception e) {
                    log.error("", e);
                    session.getTransactionManager().rollback();
                } finally {
                    session.close();
                }
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
