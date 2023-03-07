package kz.kacd.sso.client.config;

import kz.kacd.sso.v1.ClientSpec;
import org.keycloak.models.RealmModel;
import org.keycloak.provider.Provider;

public interface ClientConfigurer extends Provider {

    void configure(RealmModel realm, String clientId, ClientSpec client);
}
