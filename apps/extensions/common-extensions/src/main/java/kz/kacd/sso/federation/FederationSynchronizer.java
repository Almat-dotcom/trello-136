package kz.kacd.sso.federation;

import org.keycloak.component.ComponentModel;
import org.keycloak.models.RealmModel;
import org.keycloak.provider.Provider;

public interface FederationSynchronizer extends Provider {

    void syncToKeycloak(RealmModel realm, ComponentModel ldap, ComponentModel component);

    void syncToLdap(RealmModel realm, ComponentModel ldap, ComponentModel component);
}
