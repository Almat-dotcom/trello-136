package kz.kacd.sso.k8s;

public class K8sConfig {
    private static final String ENABLED_K8S_CONFIG = "ENABLED_K8S";
    private static final String K8S_NAMESPACE = "K8S_NAMESPACE";

    public static final boolean ENABLED;
    public static final String NAMESPACE;

    static {
        String enabledStr = System.getenv(ENABLED_K8S_CONFIG);
        ENABLED = enabledStr != null && enabledStr.equals("true");
        String namespaceProp = System.getenv(K8S_NAMESPACE);
        NAMESPACE = namespaceProp == null ? "default" : namespaceProp;
    }

    private K8sConfig() {
    }
}
