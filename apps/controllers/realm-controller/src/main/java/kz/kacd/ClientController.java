package kz.kacd;

import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.api.reconciler.ControllerConfiguration;
import io.javaoperatorsdk.operator.api.reconciler.Reconciler;
import io.javaoperatorsdk.operator.api.reconciler.UpdateControl;
import kz.kacd.keycloak.KeycloakClientProvider;
import kz.kacd.realm.RealmProvider;
import kz.kacd.sso.v1.Client;
import kz.kacd.sso.v1.ClientStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

@ControllerConfiguration
public class ClientController implements Reconciler<Client> {
    private static final Logger log = LoggerFactory.getLogger(ClientController.class);

    private final RealmProvider realms;
    private final KeycloakClientProvider clientProvider;

    public ClientController(RealmProvider realms, KeycloakClientProvider clientProvider) {
        this.realms = realms;
        this.clientProvider = clientProvider;
    }

    @Override
    public UpdateControl<Client> reconcile(Client resource, Context<Client> context) throws Exception {
        log.info("Client caught! {}", resource.getMetadata().getName());

        if (shouldUpdate(resource)) {
            var realm = realms.getRealm(resource.getMetadata().getLabels().get(RealmProvider.REALM_LABEL));
            if (realm == null) {
                log.warn("Cannot find realm for client {}!", resource.getMetadata().getName());
                return UpdateControl.noUpdate();
            }
            log.info("Calling updateClient for client: {} in realm: {}", resource.getMetadata().getName(), realm.getMetadata().getName());
            var res = clientProvider.getForRealm(realm).updateClient(resource.getMetadata().getName());
            log.info("Updated client {} with status {}.", resource.getMetadata().getName(), res.getStatus());
        }

        return UpdateControl.noUpdate();
    }

    private boolean shouldUpdate(Client resource) {
        return newStatus(resource)
                || changed(resource)
                || failedRealm(resource)
                || backoffExpired(resource);
    }

    private boolean newStatus(Client resource) {
        return resource.getStatus() == null;
    }

    private boolean changed(Client resource) {
        return resource.getStatus() != null
                && (resource.getStatus().getState() == ClientStatus.State.APPLIED
                || resource.getStatus().getState() == ClientStatus.State.FAILED)
                && !resource.getStatus().getGeneration().equals(resource.getMetadata().getGeneration().toString());
    }

    private boolean failedRealm(Client resource) {
        return resource.getStatus() != null
                && resource.getStatus().getState() == ClientStatus.State.WAITING_FOR_REALM;
    }

    private boolean backoffExpired(Client resource) {
        return resource.getStatus() != null
                && resource.getStatus().getState() == ClientStatus.State.BACKOFF
                && expired(resource.getStatus().getLastApplication(), resource.getStatus().getBackoffSeconds());
    }

    private boolean expired(String date, Long seconds) {
        var parsed = LocalDateTime.parse(date);
        var now = LocalDateTime.now();
        return now.isAfter(parsed.plusSeconds(seconds));
    }
}
