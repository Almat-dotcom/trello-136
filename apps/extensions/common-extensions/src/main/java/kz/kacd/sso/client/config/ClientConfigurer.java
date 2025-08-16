package kz.kacd.sso.client.config;

import org.keycloak.models.RealmModel;
import org.keycloak.provider.Provider;

public interface ClientConfigurer extends Provider {

    void configure(RealmModel realm, String name, Object spec);
}
