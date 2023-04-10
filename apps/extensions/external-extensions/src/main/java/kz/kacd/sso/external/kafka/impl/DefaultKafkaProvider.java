package kz.kacd.sso.external.kafka.impl;

import kz.kacd.sso.external.kafka.KafkaProvider;
import kz.kacd.sso.external.kafka.KafkaTopic;
import org.jboss.logging.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultKafkaProvider implements KafkaProvider {
    private static final Logger log = Logger.getLogger(DefaultKafkaProvider.class);

    private final Map<String, KafkaTopic<?>> topics = new ConcurrentHashMap<>();

    @Override
    public <T> KafkaTopic<T> create(String topic, Class<T> itemClass) {
        if (topics.containsKey(topic)) {
            throw new IllegalStateException("Topic " + topic + " already has been created!");
        }
        DefaultKafkaTopic<T> result = new DefaultKafkaTopic<>(topic, itemClass);
        topics.put(topic, result);
        return result;
    }

    @Override
    public void close() {
        topics.forEach((k, v) -> {
            try {
                v.close();
            } catch (Exception e) {
                log.error("Error on closing topics!", e);
            }
        });
    }
}
