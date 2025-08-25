package kz.kacd.sso.client.config;

import kz.kacd.sso.v1.ClientSpec;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.provider.Provider;

public interface ClientConfigurer extends Provider {
    void configure(RealmModel realm, String clientName, ClientSpec spec);
}

