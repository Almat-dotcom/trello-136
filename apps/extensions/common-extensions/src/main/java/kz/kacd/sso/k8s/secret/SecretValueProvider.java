package kz.kacd.sso.k8s.secret;

import org.keycloak.provider.Provider;

public interface SecretValueProvider extends Provider {

    Secret findByName(String name);
}
