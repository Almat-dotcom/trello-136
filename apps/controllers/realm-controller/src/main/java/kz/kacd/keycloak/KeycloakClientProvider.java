package kz.kacd.keycloak;

import io.quarkus.oidc.client.OidcClient;
import io.quarkus.oidc.client.OidcClientConfig;
import io.quarkus.oidc.client.OidcClients;
import kz.kacd.keycloak.filter.OIDCClientFilter;
import kz.kacd.keycloak.model.KeycloakClientCredentials;
import kz.kacd.sso.v1.Realm;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.client.ClientBuilder;

@ApplicationScoped
public class KeycloakClientProvider {

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
        String name = realm.getMetadata().getName();
        String url = getUrl(realm);
        OidcClient auth = createOidcClient(name, url, credentialsProvider.find(name));
        OIDCClientFilter filter = new OIDCClientFilter(auth);
        ResteasyClient http = (ResteasyClient) ClientBuilder.newBuilder()
                .register(filter)
                .build();
        ResteasyWebTarget target = http.target(url + "/realms/" + name);
        return target.proxy(KeycloakClient.class);
    }

    private String getUrl(Realm realm) {
        if (realm.getSpec() == null || realm.getSpec().getFrontendUrl() == null) {
            return baseUrl;
        }

        return realm.getSpec().getFrontendUrl();
    }

    private OidcClient createOidcClient(String realmName, String url, KeycloakClientCredentials credentials) {
        OidcClientConfig config = new OidcClientConfig();
        config.setId(realmName);
        config.setAuthServerUrl(url + "/realms/" + realmName);
        config.setClientId(credentials.id());
        config.getCredentials().setSecret(credentials.secret());
        return clients.newClient(config).await().indefinitely();
    }
}
