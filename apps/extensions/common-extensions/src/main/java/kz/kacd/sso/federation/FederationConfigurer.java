package kz.kacd.sso.federation;

import org.keycloak.models.RealmModel;
import org.keycloak.provider.Provider;

public interface FederationConfigurer extends Provider {

    void configure(RealmModel realm, Object spec);
}
