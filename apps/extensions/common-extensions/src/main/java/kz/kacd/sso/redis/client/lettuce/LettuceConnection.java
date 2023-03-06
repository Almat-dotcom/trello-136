package kz.kacd.sso.redis.client.lettuce;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.jboss.logging.Logger;

import java.util.concurrent.atomic.AtomicReference;

public class LettuceConnection {
    private static final Logger log = Logger.getLogger(LettuceConnection.class);

    private final RedisClient client;
    private final AtomicReference<StatefulRedisConnection<String, String>> connection = new AtomicReference<>();

    public LettuceConnection(String host, String port, int db) {
        this.client = RedisClient.create(
                "redis://"
                        + host
                        + ":"
                        + (port != null && !port.isEmpty() ? port : "6379")
                        + "/"
                        + db
        );
    }

    public RedisCommands<String, String> getConnection() {
        if (connection.get() != null) {
            return connection.get().sync();
        }

        return acquire();
    }

    private synchronized RedisCommands<String, String> acquire() {
        if (connection.get() != null) {
            return connection.get().sync();
        }

        log.infof("Acquiring new redis connection ...");
        connection.set(client.connect());
        return connection.get().sync();
    }
}
