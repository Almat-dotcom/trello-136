package kz.kacd.sso.redis.client.lettuce;

import com.google.auto.service.AutoService;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import kz.kacd.sso.redis.client.RedisCacheProvider;
import kz.kacd.sso.redis.client.RedisCacheProviderFactory;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@AutoService(RedisCacheProviderFactory.class)
public class LettuceRedisCacheProviderFactory implements RedisCacheProviderFactory {
    private static final Logger log = LoggerFactory.getLogger(LettuceRedisCacheProviderFactory.class);

    public static final String PROVIDER_ID = "lettuce-redis";

    private static final ConcurrentHashMap<Integer, StatefulRedisConnection<String, String>> CONNECTION_REGISTRY =
            new ConcurrentHashMap<>();

    @Override
    public RedisCacheProvider create(KeycloakSession session) {
        return new LettuceRedisCacheProvider(
                CONNECTION_REGISTRY,
                createClient(),
                new LettuceMarshaller()
        );
    }

    private Function<Integer, RedisClient> createClient() {
        return db -> {
            log.info("Acquiring new connection to redis ...");
            return RedisClient.create(
                    "redis://" + getHost() + ":" + getPort() + "/" + db
            );
        };
    }

    private String getHost() {
        String env = System.getenv("REDIS_HOST");
        if (env == null) {
            env = "localhost";
        }
        return env;
    }

    private String getPort() {
        String port = System.getenv("REDIS_PORT");
        if (port == null) {
            port = "6379";
        }
        return port;
    }

    @Override
    public void init(Config.Scope config) {
        // Nothing to do
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        // Nothing to do
    }

    @Override
    public void close() {
        CONNECTION_REGISTRY.forEach((key, value) -> value.close());
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }
}
