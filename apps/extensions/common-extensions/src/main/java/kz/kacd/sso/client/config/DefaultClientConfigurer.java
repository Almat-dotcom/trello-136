package kz.kacd.sso.client.config;

import kz.kacd.sso.v1.ClientSpec;
import org.jboss.logging.Logger;
import org.keycloak.models.RealmModel;

public class DefaultClientConfigurer implements ClientConfigurer {
    private static final Logger log = Logger.getLogger(DefaultClientConfigurer.class);

    @Override
    public void configure(RealmModel realm, String clientName, ClientSpec spec) {
        log.infof("Configuring client %s in realm %s", clientName, realm.getName());
        // Временно просто логируем - будет реализовано позже
    }

    @Override
    public void close() {
        // Nothing to close
    }
}

