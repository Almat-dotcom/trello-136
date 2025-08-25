package kz.kacd.sso.v1;

public interface Realm {
    RealmSpec getSpec();
    RealmStatus getStatus();
    String getName();
}


