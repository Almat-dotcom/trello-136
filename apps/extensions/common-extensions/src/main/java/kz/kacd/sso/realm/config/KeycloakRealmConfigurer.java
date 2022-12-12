package kz.kacd.sso.realm.config;

import kz.kacd.sso.k8s.realm.K8sRealm;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.provider.Provider;
import org.keycloak.provider.ProviderEvent;

public interface KeycloakRealmConfigurer extends Provider {

    RealmModel configure(K8sRealm realm);

    RealmModel configureWithDependencies(K8sRealm realm);

    interface KeycloakRealmConfigured extends ProviderEvent {
        KeycloakSession getSession();

        RealmModel getRealm();
    }
}
