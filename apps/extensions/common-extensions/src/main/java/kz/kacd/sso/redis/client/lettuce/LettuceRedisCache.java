package kz.kacd.sso.redis.client.lettuce;

import io.lettuce.core.SetArgs;
import io.lettuce.core.api.sync.RedisCommands;
import kz.kacd.sso.redis.client.CacheConfig;
import kz.kacd.sso.redis.client.RedisCache;
import kz.kacd.sso.redis.client.RedisRecord;
import org.jboss.logging.Logger;
import reactor.util.function.Tuple2;

public class LettuceRedisCache<T> implements RedisCache<T> {
    private static final Logger log = Logger.getLogger(LettuceRedisCache.class);

    private final RedisCommands<String, String> commands;
    private final LettuceMarshaller marshaller;
    private final CacheConfig<T> config;
    private final Class<T> entityClass;

    public LettuceRedisCache(
            RedisCommands<String, String> commands,
            LettuceMarshaller marshaller,
            CacheConfig<T> config,
            Class<T> entityClass
    ) {
        this.commands = commands;
        this.marshaller = marshaller;
        this.config = config;
        this.entityClass = entityClass;
    }

    @Override
    public RedisRecord<T> get(String id, ValueProducer<T> producer) {
        RedisRecord<T> result = get(id);
        if (result != null) {
            return result;
        }
        return set(id, producer.produce());
    }

    @Override
    public RedisRecord<T> get(String id) {
        RedisRecord.RedisKey resultKey = new RedisRecord.RedisKey(config.getName(), entityClass.getName(), id);
        log.debugf("Getting value for key {} ...", resultKey);

        String value = commands.get(resultKey.toString());
        if (value == null) {
            return null;
        }
        return marshaller.unmarshall(resultKey, value, entityClass);
    }

    @Override
    public RedisRecord<T> set(String id, T value) {
        RedisRecord.RedisKey resultKey = new RedisRecord.RedisKey(config.getName(), entityClass.getName(), id);
        log.debugf("Setting value for key {} ...", resultKey);

        RedisRecord<T> record = new RedisRecord<>(resultKey, value);
        Tuple2<String, String> marhsalled = marshaller.marshall(record);
        String key = marhsalled.getT1();
        String valueStr = marhsalled.getT2();
        commands.set(key, valueStr, new SetArgs().ex(Integer.valueOf(config.getTtl()).longValue()));
        return record;
    }
}
