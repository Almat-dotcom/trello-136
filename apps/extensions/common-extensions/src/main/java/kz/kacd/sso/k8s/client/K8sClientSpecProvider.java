package kz.kacd.sso.k8s.client;

import org.keycloak.provider.Provider;

public interface K8sClientSpecProvider extends Provider {
    K8sClient findByName(String name);
}

