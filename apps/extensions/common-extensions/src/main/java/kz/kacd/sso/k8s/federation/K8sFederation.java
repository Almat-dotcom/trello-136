package kz.kacd.sso.k8s.federation;

import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderEvent;

public interface K8sFederation {

    Long STANDARD_BACKOFF = 600L;

    String getName();

    String getRealm();

    Object getSpec();

    Object getStatus();

    void applying();

    void applied();

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
