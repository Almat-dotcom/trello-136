package kz.kacd.sso.external.kafka;

public class KafkaConfig {

    private static final String KEYCLOAK_KAFKA_ENABLED = "KEYCLOAK_KAFKA_ENABLED";
    private static final String KEYCLOAK_KAFKA_BOOTSTRAP_SERVERS = "KEYCLOAK_KAFKA_BOOTSTRAP_SERVERS";
    private static final String KEYCLOAK_KAFKA_GROUP_ID = "KEYCLOAK_KAFKA_GROUP_ID";
    private static final String DEFAULT_GROUP_ID = "keycloak";

    public static boolean enabled() {
        String result = System.getenv(KEYCLOAK_KAFKA_ENABLED);
        return Boolean.TRUE.toString().equals(result);
    }

    public static String bootstrapServers() {
        return System.getenv(KEYCLOAK_KAFKA_BOOTSTRAP_SERVERS);
    }

    public static String groupId() {
        String result = System.getenv(KEYCLOAK_KAFKA_GROUP_ID);
        if (result == null) {
            return DEFAULT_GROUP_ID;
        }
        return result;
    }
}
