package kz.kacd.sso.external.k8s.realm;

import org.keycloak.provider.Provider;

public interface K8sRealmProvider extends Provider {
    K8sRealm findSpec(String name);
}
