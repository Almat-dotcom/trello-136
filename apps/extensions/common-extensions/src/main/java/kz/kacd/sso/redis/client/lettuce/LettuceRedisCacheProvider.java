package kz.kacd.sso.redis.client.lettuce;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import kz.kacd.sso.redis.client.CacheConfig;
import kz.kacd.sso.redis.client.RedisCache;
import kz.kacd.sso.redis.client.RedisCacheProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class LettuceRedisCacheProvider implements RedisCacheProvider {
    private static final Logger log = LoggerFactory.getLogger(LettuceRedisCacheProvider.class);

    private final ConcurrentHashMap<Integer, StatefulRedisConnection<String, String>> connectionRegistry;
    private final Function<Integer, RedisClient> connectionFactory;
    private final LettuceMarshaller marshaller;

    public LettuceRedisCacheProvider(
            ConcurrentHashMap<Integer, StatefulRedisConnection<String, String>> connectionRegistry,
            Function<Integer, RedisClient> connectionFactory,
            LettuceMarshaller marshaller
    ) {
        this.connectionRegistry = connectionRegistry;
        this.connectionFactory = connectionFactory;
        this.marshaller = marshaller;
    }

    @Override
    public <T> RedisCache<T> create(CacheConfig<T> config) {
        log.debug(
                "Creating new redis cache with db {}, name {} and entity {} ...",
                config.getDb(),
                config.getName(),
                config.getEntityClass().getName()
        );

        if (connectionRegistry.containsKey(config.getDb())) {
            return new LettuceRedisCache<>(
                    connectionRegistry.get(config.getDb()).sync(),
                    marshaller,
                    config,
                    config.getEntityClass()
            );
        }

        return new LettuceRedisCache<>(
                acquireNewConnection(config).sync(),
                marshaller,
                config,
                config.getEntityClass()
        );
    }

    private synchronized StatefulRedisConnection<String, String> acquireNewConnection(CacheConfig<?> config) {
        if (connectionRegistry.containsKey(config.getDb())) {
            return connectionRegistry.get(config.getDb());
        }

        RedisClient client = connectionFactory.apply(config.getDb());
        connectionRegistry.put(config.getDb(), client.connect());
        return connectionRegistry.get(config.getDb());
    }

    @Override
    public void close() {
        connectionRegistry.forEach((k, v) -> v.close());
    }
}
