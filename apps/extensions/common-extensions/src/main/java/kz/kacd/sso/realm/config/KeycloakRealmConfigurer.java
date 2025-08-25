package kz.kacd.sso.realm.config;

import kz.kacd.sso.k8s.realm.K8sRealm;
import kz.kacd.sso.v1.RealmSpec;
import org.keycloak.models.RealmModel;
import org.keycloak.provider.Provider;

public interface KeycloakRealmConfigurer extends Provider {
    RealmModel configure(RealmSpec spec);
    RealmModel configureWithDependencies(RealmSpec spec);
}

