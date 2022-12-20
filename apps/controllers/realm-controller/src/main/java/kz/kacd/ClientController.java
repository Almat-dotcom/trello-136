package kz.kacd;

import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.api.reconciler.ControllerConfiguration;
import io.javaoperatorsdk.operator.api.reconciler.Reconciler;
import io.javaoperatorsdk.operator.api.reconciler.UpdateControl;
import kz.kacd.sso.v1.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ControllerConfiguration
public class ClientController implements Reconciler<Client> {
    private static final Logger log = LoggerFactory.getLogger(ClientController.class);

    @Override
    public UpdateControl<Client> reconcile(Client resource, Context<Client> context) throws Exception {
        log.info("Client caught! {}", resource.getMetadata().getName());

        return UpdateControl.noUpdate();
    }
}
