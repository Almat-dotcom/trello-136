package kz.kacd.sso.federation;

import kz.kacd.sso.v1.FederationSpec;
import org.jboss.logging.Logger;
import org.keycloak.models.RealmModel;
import org.keycloak.provider.Provider;

public class DefaultFederationConfigurer implements FederationConfigurer {
    private static final Logger log = Logger.getLogger(DefaultFederationConfigurer.class);

    @Override
    public void configure(RealmModel realm, FederationSpec spec) {
        log.infof("DefaultFederationConfigurer.configure() called for realm: %s", 
                realm != null ? realm.getName() : "null");
        // Mock implementation - just log the call
    }

    @Override
    public void close() {
        log.infof("DefaultFederationConfigurer.close() called");
    }
}
