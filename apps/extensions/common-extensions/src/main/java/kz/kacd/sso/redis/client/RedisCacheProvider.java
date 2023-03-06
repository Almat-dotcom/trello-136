package kz.kacd.sso.redis.client;

import org.keycloak.provider.Provider;

public interface RedisCacheProvider extends Provider {

    <T> RedisCache<T> create(CacheConfig<T> config);
}
