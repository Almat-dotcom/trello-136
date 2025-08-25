package kz.kacd.sso.v1;

public interface Federation {
    FederationSpec getSpec();
    FederationStatus getStatus();
    String getName();
    String getRealm();
}

