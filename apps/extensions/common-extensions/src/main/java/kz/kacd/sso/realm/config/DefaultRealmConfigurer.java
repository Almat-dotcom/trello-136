package kz.kacd.sso.realm.config;

import kz.kacd.sso.client.admin.AdminClientProvider;
import kz.kacd.sso.k8s.realm.K8sRealm;
import kz.kacd.sso.realm.config.model.RealmConfigApplier;
import kz.kacd.sso.realm.flow.AuthFlowConfigurer;
import kz.kacd.sso.realm.flow.AuthFlowConstants;
import kz.kacd.sso.v1.Realm;
import kz.kacd.sso.v1.RealmSpec;
import kz.kacd.sso.v1.realmspec.authentication.Flows;
import org.jboss.logging.Logger;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.provider.ProviderEvent;
import org.keycloak.services.managers.RealmManager;

public class DefaultRealmConfigurer implements KeycloakRealmConfigurer {
    private static final Logger log = Logger.getLogger(DefaultRealmConfigurer.class);

    private final KeycloakSession session;

    public DefaultRealmConfigurer(KeycloakSession session) {
        this.session = session;
    }

    @Override
    public RealmModel configureWithDependencies(K8sRealm realm) {
        RealmModel result = configure(realm);

        session.getKeycloakSessionFactory().publish(realmConfigured(session, result));

        return result;
    }

    private ProviderEvent realmConfigured(KeycloakSession session, RealmModel realm) {
        return new KeycloakRealmConfigured() {
            @Override
            public KeycloakSession getSession() {
                return session;
            }

            @Override
            public RealmModel getRealm() {
                return realm;
            }
        };
    }

    @Override
    public RealmModel configure(K8sRealm realm) {
        return configure(realm.getName(), realm.getSpec());
    }

    @Override
    public RealmModel configure(Realm realm) {
        return configure(realm.getMetadata().getName(), realm.getSpec());
    }

    private RealmModel configure(String name, RealmSpec spec) {
        log.infof("Configuring realm %s ...", name);

        RealmModel existing = find(name);
        if (existing == null) {
            existing = create(name);
        }

        new RealmConfigApplier(existing, session).apply(name, spec);
        applyFlowsToRealm(existing, spec);
        createAdminClient(existing);

        return existing;
    }

    private void applyFlowsToRealm(RealmModel realm, RealmSpec spec) {
        AuthFlowConfigurer configurer = session.getProvider(AuthFlowConfigurer.class);
        configurer.configurePredefinedFlows(realm);

        if (spec == null || spec.getAuthentication() == null || spec.getAuthentication().getFlows() == null) {
            return;
        }
        Flows config = spec.getAuthentication().getFlows();

        AuthFlowConfigurer flows = session.getProvider(AuthFlowConfigurer.class);
        flows.configurePredefinedFlows(realm);
        if (config.getBrowser().equals(Flows.Browser.RESTRICTED)) {
            realm.setBrowserFlow(flows.findFlow(realm, AuthFlowConstants.RESTRICTED_BROWSER));
        } else if (config.getBrowser().equals(Flows.Browser.EXTERNAL)) {
            realm.setBrowserFlow(flows.findFlow(realm, AuthFlowConstants.EXTERNAL_LOGIN));
        }
        if (config.getRegistration().equals(Flows.Registration.EXTERNAL)) {
            realm.setRegistrationFlow(flows.findFlow(realm, AuthFlowConstants.EXTERNAL_REGISTRATION));
        }
    }

    private void createAdminClient(RealmModel realm) {
        AdminClientProvider clients = session.getProvider(AdminClientProvider.class);
        clients.configureAdminClient(realm);
    }

    private RealmModel find(String realm) {
        return session.realms().getRealmByName(realm);
    }

    private RealmModel create(String realm) {
        RealmManager manager = new RealmManager(session);
        return manager.createRealm(realm);
    }

    @Override
    public void close() {
        // Nothing to close
    }
}
