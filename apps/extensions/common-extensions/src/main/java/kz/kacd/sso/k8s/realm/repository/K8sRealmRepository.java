package kz.kacd.sso.k8s.realm.repository;

import io.fabric8.kubernetes.client.KubernetesClient;
import kz.kacd.sso.k8s.K8sConfig;
import kz.kacd.sso.k8s.realm.model.RealmList;
import kz.kacd.sso.v1.Realm;
import kz.kacd.sso.v1.RealmStatus;
import org.jboss.logging.Logger;

import java.util.Optional;

public class K8sRealmRepository {
    private static final Logger log = Logger.getLogger(K8sRealmRepository.class);

    private final KubernetesClient client;

    public K8sRealmRepository(KubernetesClient client) {
        this.client = client;
    }

    public void updateStatus(String name, RealmStatus status) {
        log.debugf("Updating status in kubernetes for %s to %s ...", name, status.getState());
        Realm existing = find(name);
        if (existing == null) {
            throw new IllegalStateException("Cannot update status for realm " + name + " because it does not exist!");
        }

        client.resource(replaceStatus(existing, status)).patchStatus();
    }

    private Realm replaceStatus(Realm source, RealmStatus status) {
        source.setStatus(status);
        return source;
    }

    public Realm find(String name) {
        log.debugf("Finding realm in kubernetes %s ...", name);
        RealmList list = client.resources(Realm.class, RealmList.class).inNamespace(K8sConfig.NAMESPACE).list();
        Optional<Realm> result = list.getItems().stream().findFirst();
        return result.orElse(null);
    }
}
