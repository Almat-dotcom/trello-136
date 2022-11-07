package kz.kacd.sso.realmcontroller.k8s.model;

import kz.kacd.sso.realmcontroller.k8s.crd.Realm;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class KeycloakRealm {

    private final Realm source;
}
