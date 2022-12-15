package kz.kacd.sso.k8s.secret;

import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.Base64;
import java.util.Map;
import java.util.stream.Collectors;

public class SecretAdapter implements Secret {

    private final Map<String, String> data;

    SecretAdapter(io.fabric8.kubernetes.api.model.Secret source) {
        data = decode(source.getData());
    }

    private Map<String, String> decode(Map<String, String> source) {
        return source.entrySet().stream()
                .map(entry -> new AbstractMap.SimpleEntry<>(entry.getKey(), base64Decode(entry.getValue()).trim()))
                .collect(Collectors.toMap(
                        AbstractMap.SimpleEntry::getKey,
                        AbstractMap.SimpleEntry::getValue
                ));
    }

    private String base64Decode(String value) {
        return new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8);
    }

    @Override
    public String get(String key) {
        return data.get(key);
    }
}
