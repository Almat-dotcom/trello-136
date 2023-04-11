package kz.kacd.sso.external.kafka;

import java.io.Closeable;
import java.util.function.Consumer;

public interface KafkaTopic<T> extends Closeable {

    String name();

    KafkaSubscription subscribe(
            Consumer<T> onNext
    );

    KafkaSubscription subscribe(
            Consumer<T> onNext,
            Runnable onComplete
    );

    KafkaSubscription subscribe(
            Consumer<T> onNext,
            Runnable onComplete,
            Consumer<Throwable> onError
    );

    KafkaSubscription subscribe(KafkaTopicSubscriber<T> subscriber);
}
