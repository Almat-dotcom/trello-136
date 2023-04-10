package kz.kacd.sso.external.kafka;

@FunctionalInterface
public interface KafkaSubscription {

    void cancel();
}
