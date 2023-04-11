package kz.kacd.sso.external.migration.account.kafka;

import com.google.auto.service.AutoService;
import kz.kacd.sso.external.kafka.KafkaProvider;
import kz.kacd.sso.external.kafka.KafkaProviderFactory;
import kz.kacd.sso.external.migration.account.AccountMigrationProvider;
import kz.kacd.sso.external.migration.account.AccountMigrationProviderFactory;
import org.jboss.logging.Logger;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.utils.PostMigrationEvent;
import org.keycloak.provider.ProviderFactory;

@AutoService(KafkaAccountMigrationProviderFactory.class)
public class KafkaAccountMigrationProviderFactory implements AccountMigrationProviderFactory {
    private static final Logger log = Logger.getLogger(KafkaAccountMigrationProviderFactory.class);

    private static final String TOPIC = "kz.kcsd.lk.drscb.persons";

    public static final String PROVIDER_ID = "kafka-account-migration";

    @Override
    public AccountMigrationProvider create(KeycloakSession session) {
        return null;
    }

    @Override
    public void init(Config.Scope config) {
        // Nothing to init
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        factory.register(event -> {
            if (event instanceof PostMigrationEvent) {
                subscribe(((PostMigrationEvent) event).getFactory());
            }
        });
    }

    private void subscribe(KeycloakSessionFactory factory) {
        ProviderFactory<KafkaProvider> f = factory.getProviderFactory(KafkaProvider.class);
        KafkaProvider kafka = f.create(null);
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
