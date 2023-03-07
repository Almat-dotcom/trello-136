package kz.kacd.sso.redis.client;

import java.util.Arrays;
import java.util.stream.Collectors;

public class RedisRecord<T> {

    private final RedisKey key;
    private final T value;

    public RedisRecord(RedisKey key, T value) {
        this.key = key;
        this.value = value;
    }

    public RedisKey getKey() {
        return key;
    }

    public T getValue() {
        return value;
    }

    public static final class RedisKey {
        private final String cacheName;
        private final String className;
        private final String identifier;

        public RedisKey(String fullKey) {
            String[] parts = fullKey.split("-");
            this.cacheName = parts[0];
            this.className = parts[1];
            this.identifier = Arrays.stream(parts).skip(2).collect(Collectors.joining("-"));
        }

        public RedisKey(String cacheName, String className, String identifier) {
            this.cacheName = cacheName;
            this.className = className;
            this.identifier = identifier;
        }

        public String getCacheName() {
            return cacheName;
        }

        public String getClassName() {
            return className;
        }

        public String getIdentifier() {
            return identifier;
        }

        @Override
        public String toString() {
            return cacheName + "-" + className + "-" + identifier;
        }
    }
}
