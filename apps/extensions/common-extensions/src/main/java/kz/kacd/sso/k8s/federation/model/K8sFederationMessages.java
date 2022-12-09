package kz.kacd.sso.k8s.federation.model;

public class K8sFederationMessages {

    public static final String FEDERATION_APPLYING = "Federation is being applying.";
    public static final String FEDERATION_APPLIED = "Federation has been applied.";
    public static final String WAITING_FOR_REALM = "Waiting for realm creation ...";

    private K8sFederationMessages() {
    }

    public static String error(Throwable e) {
        return e.getClass().getSimpleName() + ": " + e.getMessage();
    }
}
