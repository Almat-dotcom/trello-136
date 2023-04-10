package kz.kacd.sso.external.kafka.impl;

import kz.kacd.sso.external.kafka.KafkaSubscription;
import kz.kacd.sso.external.kafka.KafkaTopicSubscriber;

import java.util.function.Consumer;

public class DefaultKafkaSubscriber<T> implements KafkaTopicSubscriber<T> {

    private final Consumer<T> onNext;
    private final Runnable onComplete;
    private final Consumer<Throwable> onError;

    public DefaultKafkaSubscriber(Consumer<T> onNext, Runnable onComplete, Consumer<Throwable> onError) {
        this.onNext = onNext;
        this.onComplete = onComplete;
        this.onError = onError;
    }

    @Override
    public void onSubscribe(KafkaSubscription subscription) {
        // Nothing to do
    }

    @Override
    public void onNext(T item) {
        onNext.accept(item);
    }

    @Override
    public void onComplete() {
        onComplete.run();
    }

    @Override
    public void onError(Throwable e) {
        onError.accept(e);
    }
}
