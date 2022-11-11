package kz.kacd.sso.realmcontroller.k8s.crd.client;

import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.model.annotation.Group;
import io.fabric8.kubernetes.model.annotation.ShortNames;
import io.fabric8.kubernetes.model.annotation.Version;
import kz.kacd.sso.realmcontroller.k8s.crd.client.model.ClientSpec;
import kz.kacd.sso.realmcontroller.k8s.crd.client.model.ClientStatus;

/**
 * Represents client specification of specific app in keycloak.
 */
@Group("sso.kacd.kz")
@Version(value = "v1beta1", served = true, storage = true)
@ShortNames("clt")
public class Client extends CustomResource<ClientSpec, ClientStatus> implements Namespaced {
}
