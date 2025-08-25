package kz.kacd.sso.k8s.federation;

import kz.kacd.sso.v1.FederationSpec;
import kz.kacd.sso.v1.FederationStatus;

public interface K8sFederation {
    String getName();
    String getRealm();
    FederationSpec getSpec();
    FederationStatus getStatus();
    
    void applying();
    void applied();
    void failed(Exception e);
    void backoff(Exception e);
}

