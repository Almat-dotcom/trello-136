package kz.kacd.sso.realmcontroller.k8s.model;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class SecretDataTest {

    @Test
    void should_decode_base64_values() {
        var expected = "top-secret";
        var key = "my-secret";
        var map = Map.of(
                key,
                Base64.getEncoder().encodeToString(expected.getBytes(StandardCharsets.UTF_8))
        );

        var data = new SecretData(map);
        var actual = data.get(key);

        assertThat(actual).isEqualTo(expected);
    }
}