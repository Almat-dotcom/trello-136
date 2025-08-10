package kz.kacd.sso.k8s.client;

import org.keycloak.provider.Provider;

import java.util.stream.Stream;

public interface K8sClientSpecProvider extends Provider {

    K8sClient findByName(String name);

    Stream<K8sClient> findByRealm(String realmName);
}
