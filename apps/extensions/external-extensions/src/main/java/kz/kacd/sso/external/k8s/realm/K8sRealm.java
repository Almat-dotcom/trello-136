package kz.kacd.sso.external.k8s.realm;

import kz.kacd.sso.external.v1.RealmStatus;

public class K8sRealm {
    private String name;
    private String realm;
    private RealmStatus status;

    public K8sRealm() {
    }

    public K8sRealm(String name, String realm) {
        this.name = name;
        this.realm = realm;
        this.status = new RealmStatus();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRealm() {
        return realm;
    }

    public void setRealm(String realm) {
        this.realm = realm;
    }

    public RealmStatus getStatus() {
        return status;
    }

    public void setStatus(RealmStatus status) {
        this.status = status;
    }

    public void applying() {
        if (status == null) {
            status = new RealmStatus();
        }
        status.setState(RealmStatus.State.APPLYING);
    }

    public void applied() {
        if (status == null) {
            status = new RealmStatus();
        }
        status.setState(RealmStatus.State.APPLIED);
    }

    public void failed(Exception e) {
        if (status == null) {
            status = new RealmStatus();
        }
        status.setState(RealmStatus.State.FAILED);
        status.setError(e.getMessage());
    }

    public void toBackOff(Exception e) {
        if (status == null) {
            status = new RealmStatus();
        }
        status.setState(RealmStatus.State.BACK_OFF);
        status.setError(e.getMessage());
    }
}
