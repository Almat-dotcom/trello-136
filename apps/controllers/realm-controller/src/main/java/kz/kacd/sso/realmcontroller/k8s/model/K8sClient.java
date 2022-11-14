package kz.kacd.sso.realmcontroller.k8s.model;

import kz.kacd.sso.realmcontroller.k8s.crd.client.Client;
import kz.kacd.sso.realmcontroller.k8s.crd.client.model.ClientSpec;
import kz.kacd.sso.realmcontroller.k8s.crd.client.model.ClientStatus;

import java.util.Map;

public record K8sClient(
        Client source,
        ClientStatus newStatus,
        Map<String, SecretData> secrets,
        K8sAction action
) {

    public K8sClient {
        if (newStatus == null) {
            newStatus = source.getStatus();
        }
    }

    public Boolean toApply() {
        return notIgnoredActions() && (newClient() || modified() || deleted());
    }

    private Boolean notIgnoredActions() {
        return action != K8sAction.BOOKMARK && action != K8sAction.ERROR;
    }

    private Boolean newClient() {
        return action == K8sAction.ADDED && newStatus == null;
    }

    private Boolean modified() {
        return action == K8sAction.MODIFIED
                && (newStatus.getState() == ClientStatus.State.APPLIED || newStatus.getState() == ClientStatus.State.FAILED);
    }

    private Boolean deleted() {
        return action == K8sAction.DELETED && newStatus.getState() == ClientStatus.State.APPLIED;
    }

    public String name() {
        return source.getMetadata().getName();
    }

    public ClientSpec spec() {
        return source.getSpec();
    }

    public K8sClient withSecrets(Map<String, SecretData> secrets) {
        return new K8sClient(source, newStatus, secrets, action);
    }

    public Client updatedResource() {
        var res = new Client();
        res.setApiVersion(source.getApiVersion());
        res.setKind(source.getKind());
        res.setMetadata(source.getMetadata());
        res.setSpec(source.getSpec());
        res.setStatus(newStatus);
        return res;
    }
}
