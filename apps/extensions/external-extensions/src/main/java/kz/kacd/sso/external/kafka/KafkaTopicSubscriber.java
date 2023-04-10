package kz.kacd.sso.external.kafka;

public interface KafkaTopicSubscriber<T> {

    void onSubscribe(KafkaSubscription subscription);

    void onNext(T item);

    void onComplete();

    void onError(Throwable e);
}
