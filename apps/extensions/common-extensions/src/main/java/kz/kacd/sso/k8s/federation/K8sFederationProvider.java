package kz.kacd.sso.k8s.federation;

import org.keycloak.provider.Provider;

public interface K8sFederationProvider extends Provider {

    K8sFederation findByName(String name);
}
