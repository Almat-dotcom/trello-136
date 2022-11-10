package kz.kacd.sso.realmcontroller.k8s.model;

import lombok.RequiredArgsConstructor;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

public record SecretData(Map<String, String> data) {
    public String get(String key) {
        return new String(Base64.getDecoder().decode(data.get(key)), StandardCharsets.UTF_8).trim();
    }
}
