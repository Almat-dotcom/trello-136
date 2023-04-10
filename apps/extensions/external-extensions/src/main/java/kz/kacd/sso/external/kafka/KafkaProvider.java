package kz.kacd.sso.external.kafka;

import org.keycloak.provider.Provider;

public interface KafkaProvider extends Provider {

    <T> KafkaTopic<T> create(String topic, Class<T> itemClass);
}
