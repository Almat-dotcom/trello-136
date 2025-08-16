package kz.kacd.sso.realm.config;

import kz.kacd.sso.k8s.realm.K8sRealm;
import org.keycloak.models.RealmModel;
import org.keycloak.provider.Provider;

public interface KeycloakRealmConfigurer extends Provider {

    RealmModel configure(K8sRealm spec);

    RealmModel configureWithDependencies(K8sRealm spec);
}
