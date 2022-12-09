package kz.kacd.sso.k8s.secret;

import org.keycloak.provider.Provider;

import java.util.Map;

public interface SecretValueProvider extends Provider {

    Secret findByName(String name);

    Secret create(String name, Map<String, String> labels, Map<String, String> data);
}
