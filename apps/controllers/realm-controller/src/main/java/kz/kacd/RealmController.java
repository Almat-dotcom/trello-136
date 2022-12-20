package kz.kacd;

import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.api.reconciler.ControllerConfiguration;
import io.javaoperatorsdk.operator.api.reconciler.Reconciler;
import io.javaoperatorsdk.operator.api.reconciler.UpdateControl;
import kz.kacd.sso.v1.Realm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ControllerConfiguration
public class RealmController implements Reconciler<Realm> {
    private static final Logger log = LoggerFactory.getLogger(RealmController.class);

    @Override
    public UpdateControl<Realm> reconcile(Realm resource, Context<Realm> context) throws Exception {
        log.info("YEP!: {}", resource.getMetadata().getName());

        return UpdateControl.noUpdate();
    }
}
