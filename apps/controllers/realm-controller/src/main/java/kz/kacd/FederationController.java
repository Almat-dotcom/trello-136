package kz.kacd;

import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.api.reconciler.ControllerConfiguration;
import io.javaoperatorsdk.operator.api.reconciler.Reconciler;
import io.javaoperatorsdk.operator.api.reconciler.UpdateControl;
import kz.kacd.keycloak.KeycloakClientProvider;
import kz.kacd.realm.RealmProvider;
import kz.kacd.sso.v1.Federation;
import kz.kacd.sso.v1.FederationStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

@ControllerConfiguration
public class FederationController implements Reconciler<Federation> {
    private static final Logger log = LoggerFactory.getLogger(FederationController.class);

    private final RealmProvider realms;
    private final KeycloakClientProvider clientProvider;

    public FederationController(RealmProvider realms, KeycloakClientProvider clientProvider) {
        this.realms = realms;
        this.clientProvider = clientProvider;
    }

    @Override
    public UpdateControl<Federation> reconcile(Federation resource, Context<Federation> context) throws Exception {
        log.info("Federation caught! {}", resource.getMetadata().getName());

        if (shouldUpdate(resource)) {
            var realm = realms.getRealm(resource.getMetadata().getLabels().get(RealmProvider.REALM_LABEL));
            if (realm == null) {
                log.warn("Cannot find realm for federation {}!", resource.getMetadata().getName());
                return UpdateControl.noUpdate();
            }
            var res = clientProvider.getForRealm(realm).updateFederation(realm.getMetadata().getName());
            log.info("Updated federation {} with status {}.", resource.getMetadata().getName(), res.getStatus());
        }

        return UpdateControl.noUpdate();
    }

    private boolean shouldUpdate(Federation resource) {
        return newStatus(resource)
                || updated(resource)
                || failedRealm(resource)
                || backoffExpired(resource);
    }

    private boolean newStatus(Federation resource) {
        return resource.getStatus() == null;
    }

    private boolean updated(Federation resource) {
        return resource.getStatus() != null
                && (resource.getStatus().getState() == FederationStatus.State.APPLIED
                || resource.getStatus().getState() == FederationStatus.State.FAILED)
                && !resource.getStatus().getGeneration().equals(resource.getMetadata().getGeneration().toString());
    }

    private boolean failedRealm(Federation resource) {
        return resource.getStatus() != null
                && resource.getStatus().getState() == FederationStatus.State.WAITING_REALM;
    }

    private boolean backoffExpired(Federation resource) {
        return resource.getStatus() != null
                && resource.getStatus().getState() == FederationStatus.State.BACK_OFF
                && expired(resource.getStatus().getLastApplication(), resource.getStatus().getBackoffSeconds());
    }

    private boolean expired(String date, Long seconds) {
        var parsed = LocalDateTime.parse(date);
        var now = LocalDateTime.now();
        return now.isAfter(parsed.plusSeconds(seconds));
    }
}
