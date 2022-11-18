package kz.kacd.sso.realmcontroller.keycloak;

import kz.kacd.sso.realmcontroller.keycloak.realm.model.RealmBuilder;
import org.keycloak.representations.idm.RealmRepresentation;

public record Realm(RealmRepresentation representation) {
    public static final String BROWSER_AUTH_FLOW = "browser";
    public static final String RESTRICTED_AUTH_FLOW = "RestrictedBrowser";
    public static final String RESTRICTED_ROLE = "restricted-access";

    public static RealmBuilder newInstance(String name) {
        var rep = new RealmRepresentation();
        rep.setRealm(name);
        return new RealmBuilder(rep);
    }

    public String id() {
        return representation.getId();
    }

    public String name() {
        return representation.getRealm();
    }

    public RealmBuilder update() {
        return new RealmBuilder(representation);
    }
}
