package kz.kacd.sso.k8s.client;

import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderEvent;

public interface K8sClient {
    Long STANDARD_BACKOFF = 60L;

    String getName();

    String getRealm();

    Object getSpec();

    Object getStatus();

    void applying();

    void applied();

    void waitingForRealm();

    void backoff(Throwable e);

    void failed(Throwable e);

    interface K8sClientApplyingEvent extends ProviderEvent {
        KeycloakSessionFactory getFactory();

        K8sClient getClient();
    }

    interface K8sClientAppliedEvent extends ProviderEvent {
        KeycloakSessionFactory getFactory();

        K8sClient getClient();
    }

    interface K8sClientDelayedEvent extends ProviderEvent {
        KeycloakSessionFactory getFactory();

        K8sClient getClient();
    }

    interface K8sClientBackOffedEvent extends ProviderEvent {
        KeycloakSessionFactory getFactory();

        K8sClient getClient();

        Throwable getCause();
    }

    interface K8sClientFailedEvent extends ProviderEvent {
        KeycloakSessionFactory getFactory();

        K8sClient getClient();

        Throwable getCause();
    }
}
