package kz.kacd.sso.k8s.realm.model;

public class K8sRealmMessages {

    public static final String REALM_IS_APPLYING = "Realm is being applied.";
    public static final String REALM_APPLIED = "Realm has been applied successfully.";

    private K8sRealmMessages() {
    }

    public static String error(Throwable e) {
        return e.getClass().getSimpleName() + ": " + e.getMessage();
    }
}
