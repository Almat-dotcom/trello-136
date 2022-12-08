package kz.kacd.sso.k8s;

public class K8sConfig {
    private static final String ENABLED_K8S_CONFIG = "ENABLED_K8S";

    public static final boolean ENABLED;

    static {
        String enabledStr = System.getenv(ENABLED_K8S_CONFIG);
        ENABLED = enabledStr != null && enabledStr.equals("true");
    }

    private K8sConfig() {
    }
}
