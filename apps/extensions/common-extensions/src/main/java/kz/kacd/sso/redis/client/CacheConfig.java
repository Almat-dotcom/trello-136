package kz.kacd.sso.redis.client;

public class CacheConfig<T> {

    private final int db;
    private final String name;
    private final Class<T> entityClass;
    private final int ttl;

    private CacheConfig(int db, String name, Class<T> entityClass, int ttl) {
        this.db = db;
        this.name = name;
        this.entityClass = entityClass;
        this.ttl = ttl;
    }

    public static <T> Builder<T> of(Class<T> entityClass) {
        return new Builder<>(entityClass);
    }

    public int getDb() {
        return db;
    }

    public String getName() {
        return name;
    }

    public Class<T> getEntityClass() {
        return entityClass;
    }

    public int getTtl() {
        return ttl;
    }

    public static final class Builder<T> {
        private int db = 0;
        private String name = "default-cache";
        private final Class<T> entityClass;
        private int ttl = 24 * 60;

        private Builder(Class<T> entityClass) {
            this.entityClass = entityClass;
        }

        public Builder<T> db(int db) {
            this.db = db;
            return this;
        }

        public Builder<T> name(String name) {
            this.name = name;
            return this;
        }

        public Builder<T> ttl(int ttl) {
            this.ttl = ttl;
            return this;
        }

        public CacheConfig<T> config() {
            return new CacheConfig<>(db, name, entityClass, ttl);
        }
    }
}
