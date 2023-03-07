package kz.kacd.sso.redis.sessions.repository;

import kz.kacd.sso.redis.client.CacheConfig;
import kz.kacd.sso.redis.client.RedisCache;
import kz.kacd.sso.redis.client.RedisCacheProvider;
import kz.kacd.sso.redis.client.RedisRecord;
import kz.kacd.sso.redis.sessions.entities.RootSessionEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class SessionRepository {
    private static final Logger log = LoggerFactory.getLogger(SessionRepository.class);

    private final RedisCache<RootSessionEntity> cache;

    public SessionRepository(int sessionLifespan, RedisCacheProvider cacheProvider) {
        this.cache = cacheProvider.create(
                CacheConfig.of(RootSessionEntity.class)
                        .name("sessions")
                        .ttl(sessionLifespan)
                        .config()
        );
    }

    public List<RootSessionEntity> search(String realmId) {
        log.debug("Searching entities by realm id {} ...", realmId);
        List<RedisRecord<RootSessionEntity>> result = cache.search(realmId);

        return result.stream().map(RedisRecord::getValue).collect(Collectors.toList());
    }

    public RootSessionEntity get(String realmId, String id) {
        log.debug("Getting root session by id {} ...", id);
        RedisRecord<RootSessionEntity> result = cache.get(realmId + "-" + id);
        if (result == null) {
            return null;
        }
        return result.getValue();
    }

    public void persist(String realmId, RootSessionEntity entity) {
        log.debug("Saving entity {} ...", entity.getId());
        cache.set(realmId + "-" + entity.getId(), entity);
    }

    public void delete(String realmId, String id) {
        log.debug("Deleting entity {} ...", id);
        cache.remove(realmId + "-" + id);
    }
}
