package kz.kacd.sso.external.kafka.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import kz.kacd.sso.external.kafka.KafkaConfig;
import kz.kacd.sso.external.kafka.KafkaSubscription;
import kz.kacd.sso.external.kafka.KafkaTopic;
import kz.kacd.sso.external.kafka.KafkaTopicSubscriber;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class DefaultKafkaTopic<T> implements KafkaTopic<T> {
    private static final Logger log = Logger.getLogger(DefaultKafkaTopic.class);

    private final String name;
    private final Class<T> itemClass;
    private final Map<String, KafkaTopicSubscriber<T>> subscribers = new ConcurrentHashMap<>();
    private final AtomicBoolean started = new AtomicBoolean(false);
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private KafkaConsumer<String, String> consumer;
    private ObjectMapper objectMapper;

    public DefaultKafkaTopic(String name, Class<T> itemClass) {
        this.name = name;
        this.itemClass = itemClass;
        objectMapper = new ObjectMapper();
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public KafkaSubscription subscribe(Consumer<T> onNext) {
        return subscribe(onNext, () -> {
        });
    }

    @Override
    public KafkaSubscription subscribe(Consumer<T> onNext, Runnable onComplete) {
        return subscribe(onNext, onComplete, e -> {
        });
    }

    @Override
    public KafkaSubscription subscribe(Consumer<T> onNext, Runnable onComplete, Consumer<Throwable> onError) {
        return subscribe(new DefaultKafkaSubscriber<>(onNext, onComplete, onError));
    }

    @Override
    public KafkaSubscription subscribe(KafkaTopicSubscriber<T> subscriber) {
        checkConsumer();

        String key = UUID.randomUUID().toString();
        subscribers.put(key, subscriber);

        KafkaSubscription subscription = () -> subscribers.remove(key);
        subscriber.onSubscribe(subscription);
        if (!started.get()) {
            checkSubscription();
        }

        return subscription;
    }

    private void checkConsumer() {
        if (consumer == null) {
            createNewConsumer();
        }
    }

    private synchronized void createNewConsumer() {
        if (consumer != null) {
            return;
        }

        Properties config = new Properties();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaConfig.bootstrapServers());
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        config.put(ConsumerConfig.GROUP_ID_CONFIG, KafkaConfig.groupId());
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        consumer = new KafkaConsumer<>(config);

        consumer.subscribe(Collections.singletonList(name));
    }

    private synchronized void checkSubscription() {
        if (!started.get()) {
            started.set(true);
            executor.execute(this::listenTopic);
        }
    }

    private void listenTopic() {
        try {
            while (true) {
                poll();
            }
        } catch (Exception e) {
            log.errorf("Error on consuming kafka topic {}!", name, e);
            subscribers.forEach((k, v) -> v.onError(e));
            subscribers.clear();
        }
    }

    private void poll() {
        log.debugf("Polling records from topic {} ...", name);
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));

        for (ConsumerRecord<String, String> entry : records) {
            log.debugf("Got record with key {} and offset {} ...", entry.key(), entry.offset());
            T value = parse(entry.value());
            subscribers.forEach((k, v) -> v.onNext(value));
        }
    }

    private T parse(String source) {
        try {
            return objectMapper.readValue(source, itemClass);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void close() throws IOException {
        executor.shutdown();
        consumer.unsubscribe();
        subscribers.forEach((k, v) -> v.onComplete());
        subscribers.clear();
    }
}
