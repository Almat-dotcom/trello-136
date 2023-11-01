package kz.kacd.sso.external.bmg.mkb;

import jakarta.ws.rs.core.HttpHeaders;
import kz.kacd.sso.external.bmg.exception.IOFailed;
import kz.kacd.sso.external.bmg.exception.UnexpectedResponseStatusException;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jboss.logging.Logger;

import java.io.IOException;

public class MkbHttpClient {
    private final Logger log = Logger.getLogger(MkbHttpClient.class);

    private final OkHttpClient client;
    private final String url;

    public MkbHttpClient(OkHttpClient client, String url) {
        this.client = client;
        this.url = url;
    }

    public boolean checkPhone(String iin, String phoneNumber, String token) {
        log.debugf("Checking phone %s for iin %s ...", phoneNumber, iin);
        String requestUrl = url + "/api/v1/person/" + iin + "/phone/" + phoneNumber;
        Request request = new Request.Builder()
                .url(requestUrl)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .get()
                .build();
        Call call = client.newCall(request);
        try (Response response = call.execute()) {
            if (response.isSuccessful()) {
                return true;
            } else if (response.code() == 404) {
                return false;
            } else {
                throw new UnexpectedResponseStatusException(response.code());
            }
        } catch (IOException e) {
            throw new IOFailed("Failed to send http request to " + requestUrl, e);
        }
    }
}
