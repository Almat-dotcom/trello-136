package kz.kacd.sso.external.migration.account.kafka;

import com.google.auto.service.AutoService;
import kz.kacd.sso.external.kafka.KafkaProvider;
import kz.kacd.sso.external.migration.account.AccountMigrationProvider;
import kz.kacd.sso.external.migration.account.AccountMigrationProviderFactory;
import kz.kacd.sso.external.migration.account.kafka.handler.AuthenticationDetailsHandler;
import kz.kacd.sso.external.migration.account.kafka.handler.DrscbAccountHandler;
import kz.kacd.sso.external.migration.account.kafka.handler.DrscbAccountRoleMapper;
import kz.kacd.sso.external.migration.account.kafka.handler.DrscbAttributesApplier;
import kz.kacd.sso.external.migration.account.kafka.handler.employee.EmployeeAccountHandler;
import kz.kacd.sso.external.migration.account.kafka.handler.employee.EmployeeUserCreator;
import kz.kacd.sso.external.migration.account.kafka.handler.employee.EmployeeUserSearcher;
import kz.kacd.sso.external.migration.account.kafka.handler.legal.LegalAccountHandler;
import kz.kacd.sso.external.migration.account.kafka.handler.legal.LegalOrganizationSearcher;
import kz.kacd.sso.external.migration.account.kafka.handler.physical.PhysicalAccountHandler;
import kz.kacd.sso.external.migration.account.kafka.handler.physical.PhysicalUserCreator;
import kz.kacd.sso.external.migration.account.kafka.handler.physical.PhysicalUserSearcher;
import kz.kacd.sso.external.migration.account.kafka.model.DrscbAccount;
import kz.kacd.sso.external.migration.account.kafka.model.DrscbAccountFactory;
import kz.kacd.sso.external.migration.account.kafka.representation.DrscbPersonRepresentation;
import org.jboss.logging.Logger;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.utils.KeycloakModelUtils;
import org.keycloak.models.utils.PostMigrationEvent;
import org.keycloak.provider.ProviderFactory;

@AutoService(AccountMigrationProviderFactory.class)
public class KafkaAccountMigrationProviderFactory implements AccountMigrationProviderFactory {
    public static final String PROVIDER_ID = "kafka-account-migration";
    private static final Logger log = Logger.getLogger(KafkaAccountMigrationProviderFactory.class);
    private static final String TOPIC = "kz.kcsd.lk.drscb.persons";

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
        kafka.create(TOPIC, DrscbPersonRepresentation.class)
                .subscribe(it -> onAccount(factory, it), () -> {
                }, this::onError);
    }

    private void onAccount(KeycloakSessionFactory factory, DrscbPersonRepresentation representation) {
        DrscbAccount account = DrscbAccountFactory.create(representation);
        KeycloakModelUtils.runJobInTransaction(
                factory,
                session -> {
                    session.getContext().setRealm(session.realms().getRealmByName("external"));
                    DrscbAccountHandler handler = createHandler(session);
                    handler.handle(account);
                }
        );
    }

    private DrscbAccountHandler createHandler(KeycloakSession session) {
        AuthenticationDetailsHandler authenticationDetailsHandler = new AuthenticationDetailsHandler(session);
        DrscbAttributesApplier drscbAttributesApplier = new DrscbAttributesApplier();
        DrscbAccountRoleMapper drscbAccountRoleMapper = new DrscbAccountRoleMapper(session);

        PhysicalUserSearcher physicalUserSearcher = new PhysicalUserSearcher(session);
        PhysicalUserCreator physicalUserCreator = new PhysicalUserCreator(session);
        PhysicalAccountHandler physics = new PhysicalAccountHandler(
                physicalUserSearcher,
                physicalUserCreator,
                authenticationDetailsHandler,
                drscbAttributesApplier,
                drscbAccountRoleMapper
        );

        LegalOrganizationSearcher legalOrganizationSearcher = new LegalOrganizationSearcher(session);
        LegalAccountHandler legalAccountHandler = new LegalAccountHandler(legalOrganizationSearcher);

        EmployeeUserSearcher employeeUserSearcher = new EmployeeUserSearcher(legalOrganizationSearcher);
        EmployeeUserCreator employeeUserCreator = new EmployeeUserCreator(session);
        EmployeeAccountHandler employeeAccountHandler = new EmployeeAccountHandler(
                employeeUserSearcher,
                employeeUserCreator,
                authenticationDetailsHandler,
                drscbAttributesApplier,
                drscbAccountRoleMapper
        );

        return new DrscbAccountHandler(physics, legalAccountHandler, employeeAccountHandler);
    }

    private void onError(Throwable e) {
        log.errorf("Error on listening %s topic! %s", TOPIC, e.getClass().getName() + ": " + e.getMessage());
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
