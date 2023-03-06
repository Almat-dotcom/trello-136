package kz.kacd.sso.redis.client;

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

        public RedisKey(String cacheName, String className, String identifier) {
            this.cacheName = cacheName;
            this.className = className;
            this.identifier = identifier;
        }

        static <T> RedisKey of(String cacheName, Class<T> clazz, String identifier) {
            return new RedisKey(cacheName, clazz.getName(), identifier);
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
