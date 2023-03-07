package kz.kacd.sso.redis.client;

import java.util.List;

public interface RedisCache<T> {

    RedisRecord<T> get(String id, ValueProducer<T> producer);

    RedisRecord<T> get(String id);

    List<RedisRecord<T>> search(String idPart);

    RedisRecord<T> set(String id, T value);

    void remove(String id);

    @FunctionalInterface
    interface ValueProducer<T> {
        T produce();
    }
}
