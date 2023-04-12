package kz.kacd.sso.external.kafka;

import java.util.function.Consumer;

public interface KafkaTopic<T> {

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
