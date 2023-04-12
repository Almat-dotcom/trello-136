package kz.kacd.sso.external.migration.account.kafka.handler;

import kz.kacd.sso.external.migration.account.kafka.model.DrscbAccount;
import kz.kacd.sso.external.migration.account.kafka.model.DrscbAttributes;
import kz.kacd.sso.external.model.page.ExternalRegistrationPage;
import org.jboss.logging.Logger;
import org.keycloak.models.UserModel;

public class DrscbAttributesApplier {
    private static final Logger log = Logger.getLogger(DrscbAttributesApplier.class);

    public void apply(DrscbAccount account, UserModel targetUser) {
        log.debugf("Applying DRSCB attributes for user {} ...", targetUser.getUsername());

        targetUser.setSingleAttribute(
                ExternalRegistrationPage.FIELD_RESIDENCY,
                account.isResident() ? ExternalRegistrationPage.RESIDENT : ExternalRegistrationPage.NON_RESIDENT
        );
        if (account.getIin() != null) {
            targetUser.setSingleAttribute(ExternalRegistrationPage.FIELD_IIN, account.getIin());
        }
        if (account.getFirstName() != null) {
            targetUser.setFirstName(account.getFirstName());
        }
        if (account.getLastName() != null) {
            targetUser.setLastName(account.getLastName());
        }
        if (account.getMiddleName() != null) {
            targetUser.setSingleAttribute(ExternalRegistrationPage.FIELD_MIDDLE_NAME, account.getMiddleName());
        }
        targetUser.setEnabled(account.isEnabled());
        targetUser.setSingleAttribute(DrscbAttributes.ID, account.getId());
        if (account.getRegisteredAt() != null) {
            targetUser.setSingleAttribute(DrscbAttributes.REG_DATE, account.getRegisteredAt().toString());
        }
        if (account.getEndedAt() != null) {
            targetUser.setSingleAttribute(DrscbAttributes.END_DATE, account.getEndedAt().toString());
        }
    }

}
