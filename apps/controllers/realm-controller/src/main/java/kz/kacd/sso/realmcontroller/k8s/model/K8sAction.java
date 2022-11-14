package kz.kacd.sso.realmcontroller.k8s.model;

import io.fabric8.kubernetes.client.Watcher;

public enum K8sAction {
    ADDED,
    MODIFIED,
    DELETED,
    ERROR,
    BOOKMARK;

    public static K8sAction from(Watcher.Action source) {
        return K8sAction.valueOf(source.name());
    }
}
