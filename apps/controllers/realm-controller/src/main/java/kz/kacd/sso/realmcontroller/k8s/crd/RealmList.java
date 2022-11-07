package kz.kacd.sso.realmcontroller.k8s.crd;

import io.fabric8.kubernetes.api.model.KubernetesResource;
import io.fabric8.kubernetes.api.model.KubernetesResourceList;
import io.fabric8.kubernetes.api.model.ListMeta;
import io.fabric8.kubernetes.model.annotation.Group;
import io.fabric8.kubernetes.model.annotation.Version;
import lombok.Data;

import java.util.List;

@Group("sso.kacd.kz")
@Version(value = "v1beta1", served = true, storage = true)
@Data
public class RealmList implements KubernetesResource, KubernetesResourceList<Realm> {
    private ListMeta metadata;
    private List<Realm> items;
}
