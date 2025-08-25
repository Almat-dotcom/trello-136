package kz.kacd.sso.realm.config;

import kz.kacd.sso.v1.RealmSpec;
import org.jboss.logging.Logger;
import org.keycloak.models.RealmModel;

public class DefaultKeycloakRealmConfigurer implements KeycloakRealmConfigurer {
    private static final Logger log = Logger.getLogger(DefaultKeycloakRealmConfigurer.class);

    @Override
    public RealmModel configure(RealmSpec spec) {
        log.infof("Configuring realm with spec: %s", spec);
        // Временно возвращаем null - будет реализовано позже
        return null;
    }

    @Override
    public RealmModel configureWithDependencies(RealmSpec spec) {
        log.infof("Configuring realm with dependencies: %s", spec);
        // Временно возвращаем null - будет реализовано позже
        return null;
    }

    @Override
    public void close() {
        // Nothing to close
    }
}

