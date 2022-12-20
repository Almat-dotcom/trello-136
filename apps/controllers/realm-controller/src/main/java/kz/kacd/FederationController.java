package kz.kacd;

import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.api.reconciler.ControllerConfiguration;
import io.javaoperatorsdk.operator.api.reconciler.Reconciler;
import io.javaoperatorsdk.operator.api.reconciler.UpdateControl;
import kz.kacd.sso.v1.Federation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ControllerConfiguration
public class FederationController implements Reconciler<Federation> {
    private static final Logger log = LoggerFactory.getLogger(FederationController.class);

    @Override
    public UpdateControl<Federation> reconcile(Federation resource, Context<Federation> context) throws Exception {
        log.info("Federation caught! {}", resource.getMetadata().getName());
        
        return UpdateControl.noUpdate();
    }
}
