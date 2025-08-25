package kz.kacd.sso.k8s.client;

import kz.kacd.sso.v1.ClientSpec;
import kz.kacd.sso.v1.ClientStatus;

public interface K8sClient {
    String getName();
    String getRealm();
    ClientSpec getSpec();
    ClientStatus getStatus();
    
    void applying();
    void applied();
    void failed(Exception e);
    void backoff(Exception e);
}

