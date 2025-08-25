package kz.kacd.sso.federation;

import kz.kacd.sso.v1.FederationSpec;
import org.keycloak.models.RealmModel;
import org.keycloak.provider.Provider;

public interface FederationConfigurer extends Provider {
    void configure(RealmModel realm, FederationSpec spec);
}

