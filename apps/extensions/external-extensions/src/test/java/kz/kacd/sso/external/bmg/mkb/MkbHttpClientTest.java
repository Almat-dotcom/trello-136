package kz.kacd.sso.external.bmg.mkb;

import kz.kacd.sso.external.bmg.exception.UnexpectedResponseStatusException;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MkbHttpClientTest {

    @Mock
    OkHttpClient client;
    @Mock
    Call call;
    @Mock
    Response response;

    @Test
    void should_send_correct_request() throws Exception {
        String iin = "123456789012";
        String phone = "77777777777";
        ArgumentCaptor<Request> captor = ArgumentCaptor.forClass(Request.class);
        given(client.newCall(captor.capture())).willReturn(call);
        given(call.execute()).willReturn(response);
        given(response.code()).willReturn(404);

        new MkbHttpClient(client, "https://test.host").checkPhone(iin, phone, "");

        assertThat(captor.getValue().url().toString()).contains(iin, phone);
    }

    @Test
    void should_retrieve_positive_response() throws Exception {
        String iin = "123456789012";
        String phone = "77777777777";
        given(client.newCall(any())).willReturn(call);
        given(call.execute()).willReturn(response);
        given(response.isSuccessful()).willReturn(true);

        boolean actual = new MkbHttpClient(client, "https://test.host").checkPhone(iin, phone, "");

        assertThat(actual).isTrue();
    }

    @Test
    void should_retrieve_negative_response() throws Exception {
        String iin = "123456789012";
        String phone = "77777777777";
        given(client.newCall(any())).willReturn(call);
        given(call.execute()).willReturn(response);
        given(response.isSuccessful()).willReturn(false);
        given(response.code()).willReturn(404);

        boolean actual = new MkbHttpClient(client, "https://test.host").checkPhone(iin, phone, "");

        assertThat(actual).isFalse();
    }

    @Test
    void should_throw_unexpected_status() throws Exception {
        String iin = "123456789012";
        String phone = "77777777777";
        given(client.newCall(any())).willReturn(call);
        given(call.execute()).willReturn(response);
        given(response.isSuccessful()).willReturn(false);
        given(response.code()).willReturn(500);

        MkbHttpClient mkb = new MkbHttpClient(client, "https://test.host");

        assertThatThrownBy(() -> mkb.checkPhone(iin, phone, ""))
                .isInstanceOf(UnexpectedResponseStatusException.class);
    }
}