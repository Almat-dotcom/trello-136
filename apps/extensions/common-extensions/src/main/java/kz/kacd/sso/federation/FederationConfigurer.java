package kz.kacd.sso.federation;

import kz.kacd.sso.v1.FederationSpec;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.provider.Provider;
import org.keycloak.provider.ProviderEvent;

public interface FederationConfigurer extends Provider {

    void configure(RealmModel realm, FederationSpec spec);

    ComponentModel findLdap(RealmModel realm);

    void addRoleMapping(RealmModel realm, ComponentModel parent, String client, String dn);

    interface GroupsOrRolesMapperConfigured extends ProviderEvent {
        KeycloakSession getSession();

        ComponentModel getMapper();

        boolean syncToLdap();
    }

    interface FederationsConfigured extends ProviderEvent {
        KeycloakSession getSession();

        RealmModel getRealm();
    }
}
