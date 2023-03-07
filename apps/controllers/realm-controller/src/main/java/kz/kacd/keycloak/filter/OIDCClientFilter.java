package kz.kacd.keycloak.filter;

import io.quarkus.oidc.client.OidcClient;
import io.quarkus.oidc.client.Tokens;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import java.io.IOException;

public class OIDCClientFilter implements ClientRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(OIDCClientFilter.class);

    private final OidcClient client;
    private Tokens tokens;

    public OIDCClientFilter(OidcClient client) {
        this.client = client;
    }

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        log.debug("Filtering client request ...");
        requestContext.getHeaders().add(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken());
    }

    private String getAccessToken() {
        if (tokens == null) {
            log.debug("Issuing new tokens ...");
            tokens = client.getTokens().await().indefinitely();
            return tokens.getAccessToken();
        }

        if (tokens.isAccessTokenExpired()) {
            log.debug("Access token has expired! Refreshing it ...");
            tokens = client.refreshTokens(tokens.getRefreshToken()).await().indefinitely();
        }

        return tokens.getAccessToken();
    }
}
