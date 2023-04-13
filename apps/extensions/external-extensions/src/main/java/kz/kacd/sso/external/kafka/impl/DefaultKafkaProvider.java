package kz.kacd.sso.external.kafka.impl;

import kz.kacd.sso.external.kafka.KafkaProvider;
import kz.kacd.sso.external.kafka.KafkaTopic;

public class DefaultKafkaProvider implements KafkaProvider {

    @Override
    public <T> KafkaTopic<T> create(String topic, Class<T> itemClass) {
        return new DefaultKafkaTopic<>(topic, itemClass);
    }

    @Override
    public void close() {
        // Nothing to close
    }
}
