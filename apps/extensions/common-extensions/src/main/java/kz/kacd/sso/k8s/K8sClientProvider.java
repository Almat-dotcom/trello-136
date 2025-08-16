package kz.kacd.sso.k8s;

import org.keycloak.provider.Provider;

public interface K8sClientProvider extends Provider {

    Object getClient();
}
