package kz.kacd;

import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.api.reconciler.ControllerConfiguration;
import io.javaoperatorsdk.operator.api.reconciler.Reconciler;
import io.javaoperatorsdk.operator.api.reconciler.UpdateControl;
import kz.kacd.keycloak.KeycloakClientProvider;
import kz.kacd.sso.v1.Realm;
import kz.kacd.sso.v1.RealmStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.inject.Inject;
import java.time.LocalDateTime;

@ControllerConfiguration
public class RealmController implements Reconciler<Realm> {
    private static final Logger log = LoggerFactory.getLogger(RealmController.class);

    private final KeycloakClientProvider provider;

    @Inject
    public RealmController(KeycloakClientProvider provider) {
        this.provider = provider;
    }

    @Override
    public UpdateControl<Realm> reconcile(Realm resource, Context<Realm> context) throws Exception {
        log.info("Reconciling realm {} ...", resource.getMetadata().getName());

        if (shouldUpdate(resource)) {
            var client = provider.getForRealm(resource);
            var response = client.updateRealm(resource.getMetadata().getName());
            log.info("Updated realm {} with status {}.", resource.getMetadata().getName(), response.getStatus());
        }

        return UpdateControl.noUpdate();
    }

    private boolean shouldUpdate(Realm resource) {
        return newStatus(resource)
                || changed(resource)
                || backoffExpired(resource);
    }

    private boolean newStatus(Realm resource) {
        return resource.getStatus() == null;
    }

    private boolean changed(Realm resource) {
        return resource.getStatus() != null
                && (resource.getStatus().getState() == RealmStatus.State.APPLIED
                || resource.getStatus().getState() == RealmStatus.State.FAILED)
                && !resource.getStatus().getGeneration().equals(resource.getMetadata().getGeneration().toString());
    }

    private boolean backoffExpired(Realm resource) {
        return resource.getStatus() != null
                && resource.getStatus().getState() == RealmStatus.State.BACK_OFF
                && expired(resource.getStatus().getLastApplication(), resource.getStatus().getBackoffSeconds());
    }

    private boolean expired(String date, Long seconds) {
        var parsed = LocalDateTime.parse(date);
        var now = LocalDateTime.now();
        return now.isAfter(parsed.plusSeconds(seconds));
    }
}
