package kz.kacd.sso.k8s.federation;

import org.keycloak.provider.Provider;

import java.util.stream.Stream;

public interface K8sFederationProvider extends Provider {

    K8sFederation findByName(String name);

    Stream<K8sFederation> findByRealm(String name);
}
