package kz.kacd.sso.http;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.jboss.logging.Logger;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class HttpClientManager {
    private static final Logger log = Logger.getLogger(HttpClientManager.class);
    private static final OkHttpClient httpClient;
    
    static {
        httpClient = new OkHttpClient.Builder()
                .connectionPool(new ConnectionPool(10, 5, TimeUnit.MINUTES))
                .connectTimeout(Duration.ofSeconds(10))
                .readTimeout(Duration.ofSeconds(30))
                .writeTimeout(Duration.ofSeconds(30))
                .retryOnConnectionFailure(true)
                .build();
        
        log.infof("HttpClientManager initialized with optimized settings");
    }
    
    public static OkHttpClient getClient() {
        return httpClient;
    }
    
    public static void shutdown() {
        log.infof("HttpClientManager shutting down");
        httpClient.dispatcher().executorService().shutdown();
        httpClient.connectionPool().evictAll();
    }
}



