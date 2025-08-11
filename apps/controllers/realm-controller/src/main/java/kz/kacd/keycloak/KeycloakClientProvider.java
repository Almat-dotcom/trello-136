package kz.kacd.keycloak;

import io.quarkus.oidc.client.OidcClient;
import io.quarkus.oidc.client.OidcClientConfig;
import io.quarkus.oidc.client.OidcClients;
import kz.kacd.keycloak.filter.OIDCClientFilter;
import kz.kacd.keycloak.model.KeycloakClientCredentials;
import kz.kacd.sso.v1.Realm;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.client.ClientBuilder;

@ApplicationScoped
public class KeycloakClientProvider {
    private static final Logger log = LoggerFactory.getLogger(KeycloakClientProvider.class);

    private final String baseUrl;
    private final OidcClients clients;
    private final ClientCredentialsProvider credentialsProvider;

    public KeycloakClientProvider(
            @ConfigProperty(name = "realm.controller.keycloak.default-url") String baseUrl,
            OidcClients clients,
            ClientCredentialsProvider credentialsProvider) {
        this.baseUrl = baseUrl;
        this.clients = clients;
        this.credentialsProvider = credentialsProvider;
    }

    public KeycloakClient getForRealm(Realm realm) {
        var name = realm.getMetadata().getName();
        var url = getUrl(realm);
        var auth = createOidcClient(name, url, credentialsProvider.find(name));
        var filter = new OIDCClientFilter(auth);
        var http = (ResteasyClient) ClientBuilder.newBuilder()
                .register(filter)
                .build();
        var targetUrl = url + "/realms/" + name + "/k8s-config";
        log.info("Creating KeycloakClient for URL: {}", targetUrl);
        var target = http.target(targetUrl);
        return target.proxy(KeycloakClient.class);
    }

    private String getUrl(Realm realm) {
        if (realm.getSpec() == null || realm.getSpec().getFrontendUrl() == null) {
            return baseUrl;
        }

        return realm.getSpec().getFrontendUrl();
    }

    private OidcClient createOidcClient(String realmName, String url, KeycloakClientCredentials credentials) {
        var config = new OidcClientConfig();
        config.setId(realmName);
        config.setAuthServerUrl(url + "/realms/" + realmName);
        config.setClientId(credentials.id());
        config.getCredentials().setSecret(credentials.secret());
        return clients.newClient(config).await().indefinitely();
    }
}
