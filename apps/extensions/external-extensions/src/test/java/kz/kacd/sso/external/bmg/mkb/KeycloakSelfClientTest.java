package kz.kacd.sso.external.bmg.mkb;

import kz.kacd.sso.external.bmg.exception.IOFailed;
import kz.kacd.sso.external.bmg.exception.RetrieveTokenFailed;
import okhttp3.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class KeycloakSelfClientTest {

    @Mock
    OkHttpClient client;
    @Mock
    Call call;
    @Mock
    Response response;
    @Mock
    ResponseBody body;

    @Test
    void should_call_well_known_configuration() throws Exception {
        String issuer = "https://internal-dev.dev.kacd.kz/realms/internal";
        String expected = issuer + "/protocol/openid-connect/token";
        ArgumentCaptor<Request> captor = ArgumentCaptor.forClass(Request.class);
        given(client.newCall(captor.capture())).willReturn(call);
        given(call.execute()).willReturn(response);
        given(response.isSuccessful()).willReturn(true);
        given(response.body()).willReturn(body);
        given(body.byteStream()).willReturn(new ByteArrayInputStream(
                ("{\"token_endpoint\":\"" + expected + "\"}").getBytes(StandardCharsets.UTF_8)
        ));

        String actual = new KeycloakSelfClient(client).getTokenUrl(issuer);

        assertThat(actual).isEqualTo(expected);
        assertThat(captor.getValue().url()).hasToString(issuer + "/.well-known/openid-configuration");
    }

    @Test
    void should_return_access_token() throws Exception {
        String url = "https://some.host";
        String clientId = "my-client";
        String clientSecret = "secret";
        String scope = "profile";
        String expected = "my-token";
        given(client.newCall(any())).willReturn(call);
        given(call.execute()).willReturn(response);
        given(response.isSuccessful()).willReturn(true);
        given(response.body()).willReturn(body);
        given(body.byteStream()).willReturn(new ByteArrayInputStream(
                ("{\"access_token\":\"" + expected + "\"}").getBytes(StandardCharsets.UTF_8)
        ));

        String actual = new KeycloakSelfClient(client).getToken(url, clientId, clientSecret, scope);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void should_throw_retrieve_token_failure() throws Exception {
        String issuer = "https://internal-dev.dev.kacd.kz/realms/internal";
        given(client.newCall(any())).willReturn(call);
        given(call.execute()).willReturn(response);
        given(response.isSuccessful()).willReturn(false);
        given(response.code()).willReturn(500);

        KeycloakSelfClient keycloak = new KeycloakSelfClient(client);

        assertThatThrownBy(() ->
                keycloak.getTokenUrl(issuer)
        ).isInstanceOf(RetrieveTokenFailed.class);
    }

    @Test
    void should_throw_io_failed() throws Exception {
        String issuer = "https://internal-dev.dev.kacd.kz/realms/internal";
        given(client.newCall(any())).willReturn(call);
        given(call.execute()).willThrow(new IOException());

        KeycloakSelfClient keycloak = new KeycloakSelfClient(client);

        assertThatThrownBy(() -> keycloak.getTokenUrl(issuer))
                .isInstanceOf(IOFailed.class);
    }

}