package kz.kacd.sso.k8s.federation.repository;

import io.fabric8.kubernetes.client.KubernetesClient;
import kz.kacd.sso.k8s.K8sConfig;
import kz.kacd.sso.k8s.federation.model.FederationList;
import kz.kacd.sso.k8s.realm.K8sRealm;
import kz.kacd.sso.v1.Federation;
import kz.kacd.sso.v1.FederationStatus;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.Optional;

public class K8sFederationRepository {
    private static final Logger log = Logger.getLogger(K8sFederationRepository.class);

    private final KubernetesClient client;

    public K8sFederationRepository(KubernetesClient client) {
        this.client = client;
    }

    public void updateStatus(String name, FederationStatus status) {
        log.debugf("Updating status in kubernetes for %s to %s ...", name, status.getState());
        Federation existing = find(name);
        if (existing == null) {
            throw new IllegalStateException("Cannot update status for federation " + name + " because it does not exist!");
        }

        client.resource(replaceStatus(existing, status)).patchStatus();
    }

    private Federation replaceStatus(Federation source, FederationStatus status) {
        source.setStatus(status);
        return source;
    }

    public Federation find(String name) {
        log.debugf("Finding federation in kubernetes %s ...", name);
        FederationList list = client.resources(Federation.class, FederationList.class)
                .inNamespace(K8sConfig.NAMESPACE)
                .list();
        Optional<Federation> result = list.getItems().stream().findFirst();
        return result.orElse(null);
    }

    public List<Federation> findByRealm(String realmName) {
        log.debugf("Finding federations by realm %s ...", realmName);
        FederationList list = client.resources(Federation.class, FederationList.class)
                .inNamespace(K8sConfig.NAMESPACE)
                .withLabel(K8sRealm.REALM_LABEL, realmName)
                .list();
        return list.getItems();
    }
}
