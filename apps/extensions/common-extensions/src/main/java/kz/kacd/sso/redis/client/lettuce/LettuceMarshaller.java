package kz.kacd.sso.redis.client.lettuce;

import com.fasterxml.jackson.databind.ObjectMapper;
import kz.kacd.sso.redis.client.RedisRecord;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

public class LettuceMarshaller {

    private final ObjectMapper mapper = new ObjectMapper();

    public <T> Tuple2<String, String> marshall(RedisRecord<T> redisRecord) {
        try {
            String key = redisRecord.getKey().toString();
            String value = mapper.writeValueAsString(redisRecord.getValue());
            return Tuples.of(key, value);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public <T> RedisRecord<T> unmarshall(RedisRecord.RedisKey key, String value, Class<T> clazz) {
        try {
            T result = mapper.readValue(value, clazz);
            return new RedisRecord<>(key, result);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
