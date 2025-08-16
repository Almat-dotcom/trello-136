package kz.kacd.sso.k8s;

import org.jboss.logging.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Configuration utility for K8s providers
 */
public class K8sProviderConfig {
    private static final Logger log = Logger.getLogger(K8sProviderConfig.class);
    
    private static final Properties properties = new Properties();
    
    static {
        try (InputStream input = K8sProviderConfig.class.getClassLoader()
                .getResourceAsStream("application.properties")) {
            if (input != null) {
                properties.load(input);
                log.debug("Loaded K8s provider configuration");
            } else {
                log.warn("application.properties not found, using defaults");
            }
        } catch (IOException e) {
            log.warn("Failed to load application.properties", e);
        }
    }
    
    public static String getK8sClientProvider() {
        return properties.getProperty("k8s.client.provider", "isolated-k8s-client-provider");
    }
    
    public static String getK8sClientSpecProvider() {
        return properties.getProperty("k8s.client.spec.provider", "isolated-k8s-client-provider");
    }
    
    public static boolean isIsolatedProviderEnabled() {
        String provider = getK8sClientProvider();
        return "isolated-k8s-client-provider".equals(provider);
    }
}

