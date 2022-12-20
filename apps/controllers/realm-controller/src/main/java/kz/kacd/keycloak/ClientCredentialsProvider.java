package kz.kacd.keycloak;

import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.client.KubernetesClient;
import kz.kacd.keycloak.model.KeycloakClientCredentials;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@ApplicationScoped
public class ClientCredentialsProvider {

    private final KubernetesClient client;
    private final String namespace;

    public ClientCredentialsProvider(
            KubernetesClient client,
            @ConfigProperty(name = "realm.controller.namespace") String namespace
    ) {
        this.client = client;
        this.namespace = namespace;
    }

    public KeycloakClientCredentials find(String realmName) {
        Secret secret = client.secrets().inNamespace(namespace).withName(realmName + "-admin-client").get();
        if (secret == null) {
            throw new IllegalStateException("Cannot find secret " + realmName + "-admin-client in namespace " + namespace + "!");
        }
        return convert(secret);
    }

    private KeycloakClientCredentials convert(Secret secret) {
        return new KeycloakClientCredentials(
                decode(secret.getData().get("client_id")),
                decode(secret.getData().get("client_secret"))
        );
    }

    private String decode(String source) {
        return new String(Base64.getDecoder().decode(source.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
    }
}
