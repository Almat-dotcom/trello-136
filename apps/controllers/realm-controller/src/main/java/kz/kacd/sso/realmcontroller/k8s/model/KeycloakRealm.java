package kz.kacd.sso.realmcontroller.k8s.model;

import kz.kacd.sso.realmcontroller.k8s.crd.realm.Realm;
import kz.kacd.sso.realmcontroller.k8s.crd.realm.model.RealmStatus;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public record KeycloakRealm(
        Realm source,
        RealmStatus newStatus,
        K8sAction action
) {

    public KeycloakRealm {
        if (newStatus == null) {
            newStatus = source.getStatus();
        }
    }

    public Boolean toApply() {
        return notIgnoredActions() && (newRealm() || modified() || deleted());
    }

    private Boolean notIgnoredActions() {
        return action != K8sAction.BOOKMARK && action != K8sAction.ERROR;
    }

    private Boolean newRealm() {
        return action == K8sAction.ADDED && status() == null;
    }

    private Boolean modified() {
        return endStates() && changedGeneration() && action == K8sAction.MODIFIED;
    }

    private Boolean endStates() {
        return status() != null
                && (status().getState() == RealmStatus.RealmState.APPLIED || status().getState() == RealmStatus.RealmState.FAILED);
    }

    private Boolean changedGeneration() {
        return !source.getMetadata().getGeneration().toString().equals(status().getGeneration());
    }

    private Boolean deleted() {
        return action == K8sAction.DELETED;
    }

    public String getName() {
        return source.getMetadata().getName();
    }

    public RealmStatus status() {
        return newStatus();
    }

    public Realm updatedResource() {
        var result = new Realm();
        result.setMetadata(source.getMetadata());
        result.setSpec(source.getSpec());
        result.setStatus(newStatus);
        result.setKind(source.getKind());
        result.setApiVersion(source.getApiVersion());
        return result;
    }

    public KeycloakRealm detected() {
        if (
                newStatus != null
                        && newStatus.getState() == RealmStatus.RealmState.APPLYING
        ) {
            throw new IllegalStateException("To be detected keycloak realm should have null state!");
        }

        log.info("Detected realm {} in ns {} ...", source.getMetadata().getName(), source.getMetadata().getNamespace());
        return new KeycloakRealm(
                this.source,
                RealmStatus.builder()
                        .state(RealmStatus.RealmState.DETECTED)
                        .message(Messages.DETECTED)
                        .generation(generation())
                        .build(),
                this.action
        );
    }

    public KeycloakRealm applying() {
        if (newStatus == null || newStatus.getState() != RealmStatus.RealmState.DETECTED) {
            throw new IllegalStateException("To be applying realm should be detected!");
        }

        log.info("Applying realm {} in ns {} ...", source.getMetadata().getName(), source.getMetadata().getNamespace());
        return new KeycloakRealm(
                this.source,
                RealmStatus.builder()
                        .state(RealmStatus.RealmState.APPLYING)
                        .message(Messages.APPLYING)
                        .generation(generation())
                        .build(),
                this.action
        );
    }

    public KeycloakRealm applied() {
        if (newStatus == null || newStatus.getState() != RealmStatus.RealmState.APPLYING) {
            throw new IllegalStateException("To be applied realm should be in applying state!");
        }

        log.info(
                "Realm {} in ns {} has been applied.",
                source.getMetadata().getName(),
                source.getMetadata().getNamespace()
        );
        return new KeycloakRealm(
                this.source,
                RealmStatus.builder()
                        .state(RealmStatus.RealmState.APPLIED)
                        .message(Messages.APPLIED)
                        .generation(generation())
                        .build(),
                null
        );
    }

    public KeycloakRealm failed(Exception e) {
        checkStateForFailedTransition();
        log.error(
                "Realm {} in ns {} has been failed with error!",
                source.getMetadata().getName(),
                source.getMetadata().getNamespace(),
                e
        );
        return new KeycloakRealm(
                this.source,
                RealmStatus.builder()
                        .state(RealmStatus.RealmState.FAILED)
                        .message(Messages.failed(e))
                        .generation(generation())
                        .build(),
                this.action
        );
    }

    public KeycloakRealm failed(String e) {
        checkStateForFailedTransition();
        log.error(
                "Realm {} in ns {} has been failed with error: {}",
                source.getMetadata().getName(),
                source.getMetadata().getNamespace(),
                e
        );
        return new KeycloakRealm(
                this.source,
                RealmStatus.builder()
                        .state(RealmStatus.RealmState.FAILED)
                        .message(Messages.failed(e))
                        .generation(generation())
                        .build(),
                this.action
        );
    }

    private void checkStateForFailedTransition() {
        if (newStatus == null || newStatus.getState() != RealmStatus.RealmState.APPLYING) {
            throw new IllegalStateException("To be failed realm should be in applying state!");
        }
    }

    private String generation() {
        if (
                source.getMetadata() != null
                    && source.getMetadata().getGeneration() != null
        ) {
            return source.getMetadata().getGeneration().toString();
        }
        return "";
    }

    private static final class Messages {
        private static final String DETECTED = "Realm has been detected by controller.";
        private static final String APPLYING = "Controller is applying this realm.";
        private static final String APPLIED = "Realm has been applied successfully.";
        private static final String FAILED = "Realm application has been failed with error: %s";

        private static String failed(Exception e) {
            return FAILED.replaceAll(
                    "%s",
                    e.getClass().getSimpleName() + ": " + e.getMessage()
            );
        }

        private static String failed(String e) {
            return FAILED.replaceAll("%s", e);
        }
    }

}
