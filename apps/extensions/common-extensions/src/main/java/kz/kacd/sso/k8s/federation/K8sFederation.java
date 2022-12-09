package kz.kacd.sso.k8s.federation;

import kz.kacd.sso.v1.FederationSpec;
import kz.kacd.sso.v1.FederationStatus;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderEvent;

public interface K8sFederation {
    Long STANDARD_BACKOFF = 600L;

    String getName();

    FederationSpec getSpec();

    FederationStatus getStatus();

    void applying();

    void applied();

    void waitingForRealm();

    void backoff(Throwable e);

    void failed(Throwable e);

    interface K8sFederationApplyingEvent extends ProviderEvent {
        KeycloakSessionFactory getFactory();

        K8sFederation getFederation();
    }

    interface K8sFederationAppliedEvent extends ProviderEvent {
        KeycloakSessionFactory getFactory();

        K8sFederation getFederation();
    }

    interface K8sFederationDelayedEvent extends ProviderEvent {
        KeycloakSessionFactory getFactory();

        K8sFederation getFederation();
    }

    interface K8sFederationBackOffedEvent extends ProviderEvent {
        KeycloakSessionFactory getFactory();

        K8sFederation getFederation();

        Throwable getCause();
    }

    interface K8sFederationFailedEvent extends ProviderEvent {
        KeycloakSessionFactory getFactory();

        K8sFederation getFederation();

        Throwable getCause();
    }
}
