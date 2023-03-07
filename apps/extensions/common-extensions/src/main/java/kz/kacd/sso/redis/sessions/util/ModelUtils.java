package kz.kacd.sso.redis.sessions.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

public class ModelUtils {

    private ModelUtils() {
    }

    public static String generateSessionId() {
        return Base64.getEncoder().encodeToString(UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8));
    }
}
