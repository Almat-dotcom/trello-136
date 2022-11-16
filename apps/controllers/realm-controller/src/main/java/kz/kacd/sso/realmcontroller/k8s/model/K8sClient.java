package kz.kacd.sso.realmcontroller.k8s.model;

import kz.kacd.sso.realmcontroller.k8s.crd.client.Client;
import kz.kacd.sso.realmcontroller.k8s.crd.client.model.ClientSpec;
import kz.kacd.sso.realmcontroller.k8s.crd.client.model.ClientStatus;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

public record K8sClient(
        Client source,
        ClientStatus newStatus,
        Map<String, SecretData> secrets,
        K8sAction action
) {
    private static final Long STANDARD_BACKOFF = 1_800L;

    public K8sClient {
        if (newStatus == null) {
            newStatus = source.getStatus();
        }
    }

    public Boolean toApply() {
        return notIgnoredActions() && notIgnoredState() && (newClient() || modified() || deleted());
    }

    private Boolean notIgnoredActions() {
        return action != K8sAction.BOOKMARK && action != K8sAction.ERROR;
    }

    private Boolean notIgnoredState() {
        return newStatus == null || newStatus.getState() != ClientStatus.State.WAITING_FOR_REALM;
    }

    private Boolean newClient() {
        return action == K8sAction.ADDED && newStatus == null;
    }

    private Boolean modified() {
        return action == K8sAction.MODIFIED
                && newStatus != null
                && (newStatus.getState() == ClientStatus.State.APPLIED || newStatus.getState() == ClientStatus.State.FAILED)
                && !Objects.equals(source.getMetadata().getGeneration(), newStatus.getGeneration());
    }

    private Boolean deleted() {
        return action == K8sAction.DELETED && newStatus != null && newStatus.getState() == ClientStatus.State.APPLIED;
    }

    public String realmName() {
        return source.getSpec().getRealm();
    }

    public String name() {
        return source.getMetadata().getName();
    }

    public ClientSpec spec() {
        return source.getSpec();
    }

    public Boolean backoffPast() {
        return newStatus == null
                || newStatus.getBackoffSeconds() == -1
                || !newStatus.getGeneration().equals(source.getMetadata().getGeneration())
                || LocalDateTime.now().isAfter(backoffTime());
    }

    private LocalDateTime backoffTime() {
        var last = lastApplied();
        return last.plusSeconds(newStatus.getBackoffSeconds());
    }

    private LocalDateTime lastApplied() {
        return LocalDateTime.parse(newStatus.getLastApplication());
    }

    public K8sClient withSecrets(Map<String, SecretData> secrets) {
        return new K8sClient(source, newStatus, secrets, action);
    }

    public K8sClient fail(String message) {
        return new K8sClient(
                source,
                ClientStatus.builder()
                        .state(ClientStatus.State.FAILED)
                        .message(message)
                        .generation(source.getMetadata().getGeneration())
                        .lastApplication(LocalDateTime.now().toString())
                        .backoffSeconds(-1L)
                        .build(),
                secrets,
                action
        );
    }

    public K8sClient apply() {
        return new K8sClient(
                source,
                ClientStatus.builder()
                        .state(ClientStatus.State.APPLYING)
                        .message("Applying client to realm ...")
                        .generation(source.getMetadata().getGeneration())
                        .lastApplication(LocalDateTime.now().toString())
                        .backoffSeconds(-1L)
                        .build(),
                secrets,
                action
        );
    }

    public K8sClient waitRealm(String message) {
        return new K8sClient(
                source,
                ClientStatus.builder()
                        .state(ClientStatus.State.WAITING_FOR_REALM)
                        .message(message)
                        .generation(source.getMetadata().getGeneration())
                        .lastApplication(LocalDateTime.now().toString())
                        .backoffSeconds(-1L)
                        .build(),
                secrets,
                action
        );
    }

    public K8sClient backoff(String message) {
        return new K8sClient(
                source,
                ClientStatus.builder()
                        .state(ClientStatus.State.BACKOFF)
                        .message(message)
                        .generation(source.getMetadata().getGeneration())
                        .lastApplication(LocalDateTime.now().toString())
                        .backoffSeconds(STANDARD_BACKOFF)
                        .build(),
                secrets,
                action
        );
    }

    public K8sClient success() {
        return new K8sClient(
                source,
                ClientStatus.builder()
                        .state(ClientStatus.State.APPLIED)
                        .message("Client successfully applied!")
                        .generation(source.getMetadata().getGeneration())
                        .lastApplication(LocalDateTime.now().toString())
                        .backoffSeconds(-1L)
                        .build(),
                secrets,
                action
        );
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
