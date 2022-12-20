package kz.kacd;

import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.api.reconciler.ControllerConfiguration;
import io.javaoperatorsdk.operator.api.reconciler.Reconciler;
import io.javaoperatorsdk.operator.api.reconciler.UpdateControl;
import kz.kacd.keycloak.KeycloakClient;
import kz.kacd.keycloak.KeycloakClientProvider;
import kz.kacd.sso.v1.Realm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

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
        log.info("YEP!: {}", resource.getMetadata().getName());

        KeycloakClient client = provider.getForRealm(resource);

        return UpdateControl.noUpdate();
    }
}
