package kz.kacd.sso.k8s.client.repository;

import io.fabric8.kubernetes.client.KubernetesClient;
import kz.kacd.sso.k8s.K8sConfig;
import kz.kacd.sso.k8s.client.model.ClientList;
import kz.kacd.sso.k8s.realm.K8sRealm;
import kz.kacd.sso.v1.Client;
import kz.kacd.sso.v1.ClientStatus;
import org.jboss.logging.Logger;

import java.util.List;

public class K8sClientRepository {
    private static final Logger log = Logger.getLogger(K8sClientRepository.class);

    private final KubernetesClient client;

    public K8sClientRepository(KubernetesClient client) {
        this.client = client;
    }

    public void updateStatus(String name, ClientStatus status) {
        log.debugf("Updating status in kubernetes for %s to %s ...", name, status.getState());
        Client existing = find(name);
        if (existing == null) {
            throw new IllegalStateException("Cannot update status for client " + name + " because it does not exist!");
        }

        client.resource(replaceStatus(existing, status)).patchStatus();
    }

    private Client replaceStatus(Client source, ClientStatus status) {
        source.setStatus(status);
        return source;
    }

    public Client find(String name) {
        log.debugf("Finding client in kubernetes %s ...", name);
        return client.resources(Client.class, ClientList.class).inNamespace(K8sConfig.NAMESPACE).withName(name).get();
    }

    public List<Client> findByRealm(String realmName) {
        log.debugf("Finding clients by realm %s ...", realmName);
        ClientList list = client.resources(Client.class, ClientList.class).inNamespace(K8sConfig.NAMESPACE).withLabel(K8sRealm.REALM_LABEL, realmName).list();
        return list.getItems();
    }
}
