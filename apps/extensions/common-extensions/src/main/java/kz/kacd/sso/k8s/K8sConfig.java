package kz.kacd.sso.k8s;

import org.jboss.logging.Logger;

public class K8sConfig {
    private static final Logger log = Logger.getLogger(K8sConfig.class);
    private static final String ENABLED_K8S_CONFIG = "ENABLED_K8S";
    private static final String K8S_NAMESPACE = "K8S_NAMESPACE";

    public static final boolean ENABLED;
    public static final String NAMESPACE;

    static {
        String enabledStr = System.getenv(ENABLED_K8S_CONFIG);
        log.debugf("ENABLED_K8S environment variable: %s", enabledStr);
        ENABLED = enabledStr != null && enabledStr.equals("true");
        log.debugf("K8sConfig.ENABLED = %s", ENABLED);
        
        String namespaceProp = System.getenv(K8S_NAMESPACE);
        NAMESPACE = namespaceProp == null ? "default" : namespaceProp;
        log.debugf("K8sConfig.NAMESPACE = %s", NAMESPACE);
    }

    private K8sConfig() {
    }
}
