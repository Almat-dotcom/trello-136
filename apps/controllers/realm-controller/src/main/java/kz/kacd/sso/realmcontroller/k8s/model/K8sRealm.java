package kz.kacd.sso.realmcontroller.k8s.model;

import java.util.Map;

public record K8sRealm(
        KeycloakRealm realm,
        Map<String, SecretData> secrets,
        Exception e
) {
}
