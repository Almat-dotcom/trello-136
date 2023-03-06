package kz.kacd.sso.redis.client;

public interface RedisCache<T> {

    RedisRecord<T> get(String id, ValueProducer<T> producer);

    RedisRecord<T> get(String id);

    RedisRecord<T> set(String id, T value);

    @FunctionalInterface
    interface ValueProducer<T> {
        T produce();
    }
}
