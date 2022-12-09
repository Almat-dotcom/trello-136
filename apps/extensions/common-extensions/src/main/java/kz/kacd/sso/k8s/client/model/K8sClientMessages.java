package kz.kacd.sso.k8s.client.model;

public class K8sClientMessages {

    public static final String CLIENT_APPLYING = "Client is being applying.";
    public static final String CLIENT_APPLIED = "Client has been applied.";
    public static final String WAITING_FOR_REALM = "Waiting for realm creation ...";

    private K8sClientMessages() {
    }

    public static String error(Throwable e) {
        return e.getClass().getSimpleName() + ": " + e.getMessage();
    }
}
