package kz.kacd.sso.k8s.realm;

import kz.kacd.sso.v1.RealmSpec;
import kz.kacd.sso.v1.RealmStatus;

public interface K8sRealm {
    String getName();
    RealmSpec getSpec();
    RealmStatus getStatus();
    
    void applying();
    void applied();
    void failed(Exception e);
    void toBackOff(Exception e);
}

