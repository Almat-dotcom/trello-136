package kz.kacd.sso.external.bmg.mkb;

import com.fasterxml.jackson.databind.ObjectMapper;
import kz.kacd.sso.external.bmg.exception.IOFailed;
import kz.kacd.sso.external.bmg.exception.RetrieveTokenFailed;
import okhttp3.*;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.util.Map;

public class KeycloakSelfClient {
    private static final Logger log = Logger.getLogger(KeycloakSelfClient.class);

    private final OkHttpClient httpClient;

    public KeycloakSelfClient(OkHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public String getTokenUrl(String issuer) {
        log.debugf("Getting token endpoint by issuer %s ...", issuer);
        String url = issuer + "/.well-known/openid-configuration";

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        return (String) send(request).get("token_endpoint");
    }

    public String getToken(String url, String clientId, String clientSecret, String scope) {
        log.debugf("Getting access token by url %s with clientId %s", url, clientId);
        Request request = new Request.Builder()
                .url(url)
                .post(
                        new FormBody.Builder()
                                .addEncoded("grant_type", "client_credentials")
                                .addEncoded("client_id", clientId)
                                .addEncoded("client_secret", clientSecret)
                                .addEncoded("scope", scope + " openid")
                                .build()
                )
                .build();
        return (String) send(request).get("access_token");
    }

    @SuppressWarnings("unchecked")
    private Map<Object, Object> send(Request request) {
        Call call = httpClient.newCall(request);
        try(Response response = call.execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                throw new RetrieveTokenFailed("Unsuccessful response of openid configuration: " + response.code() + "!");
            }
            return new ObjectMapper().readValue(response.body().byteStream(), Map.class);
        } catch (IOException e) {
            throw new IOFailed("Failed to send http request!", e);
        }
    }
}
