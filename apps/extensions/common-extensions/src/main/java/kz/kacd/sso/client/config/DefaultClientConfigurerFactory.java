package kz.kacd.sso.client.config;

import com.google.auto.service.AutoService;
import kz.kacd.sso.federation.FederationConfigurer;
import kz.kacd.sso.k8s.K8sConfig;
import kz.kacd.sso.k8s.client.K8sClientSpecProvider;
import org.jboss.logging.Logger;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.RealmModel;
import org.slf4j.LoggerFactory;

@AutoService(ClientConfigurerFactory.class)
public class DefaultClientConfigurerFactory implements ClientConfigurerFactory {
    private static final Logger logger = Logger.getLogger(DefaultClientConfigurerFactory.class);
    private static final String PROVIDER_ID = "default-client-configurer";

    @Override
    public ClientConfigurer create(KeycloakSession session) {
        return new DefaultClientConfigurer(session);
    }

    @Override
    public void init(Config.Scope config) {
        // Nothing to init
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        factory.register(event -> {
            if (event instanceof FederationConfigurer.FederationsConfigured) {
                configureClients((FederationConfigurer.FederationsConfigured) event);
            }
        });
    }

    private void configureClients(FederationConfigurer.FederationsConfigured event) {
        logger.info("Configuring clients ALMAT");
        if (!K8sConfig.ENABLED) {
            return;
        }

        KeycloakSession session = event.getSession();
        RealmModel realm = event.getRealm();
        K8sClientSpecProvider k8s = session.getProvider(K8sClientSpecProvider.class);

        ClientConfigurer configurer = create(session);

        k8s.findByRealm(realm.getName())
                .forEach(spec -> configurer.configure(realm, spec.getName(), spec.getSpec()));
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
