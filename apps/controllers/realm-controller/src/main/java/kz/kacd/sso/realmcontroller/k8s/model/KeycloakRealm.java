package kz.kacd.sso.realmcontroller.k8s.model;

import io.fabric8.kubernetes.client.Watcher;
import kz.kacd.sso.realmcontroller.k8s.crd.Realm;
import kz.kacd.sso.realmcontroller.k8s.crd.model.RealmStatus;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public record KeycloakRealm(
        Realm source,
        RealmStatus newStatus,
        RealmAction action
) {

    public KeycloakRealm {
        if (newStatus == null) {
            newStatus = source.getStatus();
        }
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
        if (newStatus != null) {
            throw new IllegalStateException("To be detected keycloak realm should have null state!");
        }

        log.info("Detected realm {} in ns {} ...", source.getMetadata().getName(), source.getMetadata().getNamespace());
        return new KeycloakRealm(
                this.source,
                RealmStatus.builder()
                        .state(RealmStatus.RealmState.DETECTED)
                        .message(Messages.DETECTED)
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
                        .build(),
                this.action
        );
    }

    private void checkStateForFailedTransition() {
        if (newStatus == null || newStatus.getState() != RealmStatus.RealmState.APPLYING) {
            throw new IllegalStateException("To be failed realm should be in applying state!");
        }
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

    public enum RealmAction {
        ADDED,
        MODIFIED,
        DELETED,
        ERROR,
        BOOKMARK;

        public static RealmAction from(Watcher.Action source) {
            return RealmAction.valueOf(source.name());
        }
    }
}
