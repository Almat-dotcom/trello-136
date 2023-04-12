package kz.kacd.sso.external.migration.account.kafka.handler.employee;

import kz.kacd.sso.external.migration.account.kafka.handler.legal.LegalOrganizationSearcher;
import kz.kacd.sso.external.migration.account.kafka.model.DrscbAccount;
import kz.kacd.sso.external.model.OrganizationModel;
import kz.kacd.sso.external.model.PositionModel;
import kz.kacd.sso.external.model.page.ExternalRegistrationPage;
import org.jboss.logging.Logger;
import org.keycloak.models.UserModel;

public class EmployeeUserSearcher {
    private static final Logger log = Logger.getLogger(EmployeeUserSearcher.class);
    private final LegalOrganizationSearcher organizationSearcher;

    public EmployeeUserSearcher(LegalOrganizationSearcher organizationSearcher) {
        this.organizationSearcher = organizationSearcher;
    }

    public UserModel find(DrscbAccount account) {
        log.debugf("Searching employee {} ...", account.getId());

        OrganizationModel org = organizationSearcher.find(account);
        if (org == null) {
            return null;
        }

        PositionModel position;
        if (account.isResident()) {
            position = org.getPositions()
                    .filter(it ->
                            it.getUser().getUsername()
                                    .equals(account.getIin() + "-" + ExternalRegistrationPage.CLIENT_LEGAL)
                    ).findAny().orElse(null);
        } else {
            position = org.getPositions()
                    .filter(it ->
                            it.getUser().getEmail()
                                    .equals(account.getEmail())
                    ).findAny().orElse(null);
        }
        if (position != null) {
            return position.getUser();
        }
        return null;
    }
}
