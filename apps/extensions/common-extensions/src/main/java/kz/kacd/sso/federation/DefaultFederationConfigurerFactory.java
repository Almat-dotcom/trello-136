package kz.kacd.sso.federation;

import com.google.auto.service.AutoService;
import kz.kacd.sso.k8s.K8sConfig;
import kz.kacd.sso.k8s.federation.K8sFederationProvider;
import kz.kacd.sso.realm.config.KeycloakRealmConfigurer;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.RealmModel;

@AutoService(FederationConfigurerFactory.class)
public class DefaultFederationConfigurerFactory implements FederationConfigurerFactory {
    private static final String PROVIDER_ID = "default-federation-configurer";

    @Override
    public FederationConfigurer create(KeycloakSession session) {
        return new DefaultFederationConfigurer(session);
    }

    @Override
    public void init(Config.Scope config) {
        // Nothing to init
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        factory.register(event -> {
            if (event instanceof KeycloakRealmConfigurer.KeycloakRealmConfigured) {
                configureFederations((KeycloakRealmConfigurer.KeycloakRealmConfigured) event);
            }
        });
    }

    private void configureFederations(KeycloakRealmConfigurer.KeycloakRealmConfigured event) {
        if (!K8sConfig.ENABLED) {
            return;
        }

        KeycloakSession session = event.getSession();
        RealmModel realm = event.getRealm();

        K8sFederationProvider federations = session.getProvider(K8sFederationProvider.class);
        FederationConfigurer configurer = create(session);

        federations.findByRealm(realm.getName()).forEach(it ->
                configurer.configure(realm, it.getSpec())
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
