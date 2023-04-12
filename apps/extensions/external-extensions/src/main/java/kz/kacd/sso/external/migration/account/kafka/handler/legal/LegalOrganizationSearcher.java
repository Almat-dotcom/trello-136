package kz.kacd.sso.external.migration.account.kafka.handler.legal;

import kz.kacd.sso.external.migration.account.kafka.model.DrscbAccount;
import kz.kacd.sso.external.migration.account.kafka.model.DrscbAttributes;
import kz.kacd.sso.external.model.OrganizationModel;
import kz.kacd.sso.external.model.OrganizationProvider;
import org.jboss.logging.Logger;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

import java.util.Optional;

public class LegalOrganizationSearcher {
    private static final Logger log = Logger.getLogger(LegalOrganizationSearcher.class);

    private final KeycloakSession session;
    private final RealmModel realm;

    public LegalOrganizationSearcher(KeycloakSession session) {
        this.session = session;
        this.realm = session.getContext().getRealm();
    }

    public OrganizationModel find(DrscbAccount account) {
        log.debugf("Searching legal account {} ...", account.getId());

        if (account.isResident()) {
            return session.getProvider(OrganizationProvider.class).getOrganizationByBin(realm, account.getBin());
        } else {
            Optional<UserModel> employees = session.users()
                    .searchForUserByUserAttributeStream(
                            realm,
                            DrscbAttributes.LEGAL_NON_RESIDENT_IDN,
                            account.getBin()
                    ).findAny();
            if (!employees.isPresent()) {
                return null;
            }

            Optional<OrganizationModel> org = session.getProvider(OrganizationProvider.class)
                    .getUserOrganizations(realm, employees.get())
                    .findAny();
            return org.orElse(null);
        }
    }
}
