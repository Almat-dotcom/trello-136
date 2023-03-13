package kz.kacd.sso.external.sign;

import com.fasterxml.jackson.databind.ObjectMapper;
import kz.kacd.sso.external.sign.exception.InvalidSignatureException;
import kz.kacd.sso.external.sign.exception.UnexpectedSignerErrorException;
import okhttp3.*;

import java.time.Duration;

public class SignedXmlDocument {

    private final OkHttpClient client;
    private final String document;
    private final String url;
    private SignatureSubject subject;
    private Throwable error;

    public SignedXmlDocument(String source) {
        this.document = source;
        this.client = new OkHttpClient.Builder()
                .connectTimeout(Duration.ofSeconds(5))
                .readTimeout(Duration.ofSeconds(15))
                .build();
        this.url = System.getenv("KC_SIGN_NCA_URL");
    }

    public void validate() {
        try {
            Request req = new Request.Builder()
                    .url(url)
                    .post(RequestBody.create(document, MediaType.parse("application/xml")))
                    .build();

            Call call = client.newCall(req);
            try (Response response = call.execute()) {
                if (response.code() == 200 && response.body() != null) {
                    this.subject = new ObjectMapper().readValue(response.body().byteStream(), SignatureSubject.class);
                    return;
                }

                if (response.code() == 400) {
                    this.error = new InvalidSignatureException();
                    return;
                }

                this.error = new UnexpectedSignerErrorException();
            }
        } catch (Exception e) {
            error = e;
        }
    }

    public Throwable getError() {
        return error;
    }

    public String getIin() {
        return subject != null ? subject.getIin() : null;
    }

    public String getBin() {
        return subject != null ? subject.getBin() : null;
    }
}
