package kz.kacd.sso.realmcontroller.k8s.crd;

import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.model.annotation.Group;
import io.fabric8.kubernetes.model.annotation.ShortNames;
import io.fabric8.kubernetes.model.annotation.Version;
import kz.kacd.sso.realmcontroller.k8s.crd.model.RealmSpec;
import kz.kacd.sso.realmcontroller.k8s.crd.model.RealmStatus;

/**
 *
 */
@Group("sso.kacd.kz")
@Version(value = "v1beta1", served = true, storage = true)
@ShortNames("rlm")
public class Realm extends CustomResource<RealmSpec, RealmStatus> implements Namespaced {
}
