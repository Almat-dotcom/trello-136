package kz.kacd.sso.k8s.realm;

import kz.kacd.sso.v1.RealmSpec;
import kz.kacd.sso.v1.RealmStatus;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderEvent;

public interface K8sRealm {

    Long STANDARD_BACKOFF = 600L;

    String REALM_LABEL = "kz-kacd-realm-name";

    String getName();

    RealmSpec getSpec();

    RealmStatus getStatus();

    void applying();

    void applied();

    void toBackOff(Throwable e);

    void failed(Throwable e);

    interface K8sRealmApplyingEvent extends ProviderEvent {
        KeycloakSessionFactory getFactory();

        K8sRealm getRealm();
    }

    interface K8sRealmAppliedEvent extends ProviderEvent {
        KeycloakSessionFactory getFactory();

        K8sRealm getRealm();
    }

    interface K8sRealmBackOffedEvent extends ProviderEvent {
        KeycloakSessionFactory getFactory();

        K8sRealm getRealm();

        Throwable getCause();
    }

    interface K8sRealmFailedEvent extends ProviderEvent {
        KeycloakSessionFactory getFactory();

        K8sRealm getRealm();

        Throwable getCause();
    }
}
