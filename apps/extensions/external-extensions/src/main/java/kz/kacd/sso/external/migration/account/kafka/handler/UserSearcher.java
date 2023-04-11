package kz.kacd.sso.external.migration.account.kafka.handler;

import kz.kacd.sso.external.migration.account.kafka.model.DrscbAccount;
import kz.kacd.sso.external.migration.account.kafka.model.DrscbAccountContext;
import kz.kacd.sso.external.migration.account.kafka.model.DrscbAttributes;
import kz.kacd.sso.external.model.OrganizationModel;
import kz.kacd.sso.external.model.OrganizationProvider;
import kz.kacd.sso.external.model.PositionModel;
import kz.kacd.sso.external.model.page.ExternalRegistrationPage;
import org.jboss.logging.Logger;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.UserModel;

import java.util.Optional;

public class UserSearcher extends AbstractDrscbAccountHandler {
    private static final Logger log = Logger.getLogger(UserSearcher.class);

    protected UserSearcher(KeycloakSession session) {
        super(session);
    }

    @Override
    public void handle(DrscbAccountContext context) {
        log.debugf("Searching user for account {} ...", context.getAccount().getId());

        if (context.getAccount().getKind().equals(DrscbAccount.Kind.PERSONAL_ACCOUNT)) {
            searchPhysical(context);
            return;
        }

        if (context.getAccount().getKind().equals(DrscbAccount.Kind.LEGAL_ACCOUNT)) {
            searchLegal(context);
            return;
        }

        searchEmployee(context);
    }

    private void searchPhysical(DrscbAccountContext context) {
        log.debugf("Searching physical account {} ...", context.getAccount().getId());
        if (context.getAccount().isResident()) {
            searchPhysicalResident(context);
        } else {
            searchPhysicalNonResident(context);
        }
    }

    private void searchPhysicalResident(DrscbAccountContext context) {
        log.debugf("Searching physical resident account {} ...", context.getAccount().getIin());
        context.setUser(
                session.users()
                        .searchForUserByUserAttributeStream(
                                realm,
                                ExternalRegistrationPage.FIELD_IIN,
                                context.getAccount().getIin()
                        )
                        .filter(it ->
                                it.getFirstAttribute(ExternalRegistrationPage.FIELD_CLIENT_TYPE)
                                        .equals(ExternalRegistrationPage.CLIENT_PHYSICAL)
                        )
                        .findAny()
                        .orElse(null)
        );
    }

    private void searchPhysicalNonResident(DrscbAccountContext context) {
        if (!context.getAccount().emailPresent()) {
            log.warnf("Unable to identify user account {}!", context.getAccount().getId());
            return;
        }

        log.debugf("Searching non-resident user account {} ...", context.getAccount().getEmail());
        UserModel user = session.users().getUserByEmail(realm, context.getAccount().getEmail());
        if (
                user != null
                        && user.getFirstAttribute(ExternalRegistrationPage.FIELD_CLIENT_TYPE)
                                .equals(ExternalRegistrationPage.CLIENT_PHYSICAL)
        ) {
            context.setUser(user);
        }
    }

    private void searchEmployee(DrscbAccountContext context) {
        log.debugf("Searching employee {} ...", context.getAccount().getId());

        searchLegal(context);
        if (context.getOrganization() == null) {
            return;
        }

        PositionModel position;
        if (context.getAccount().isResident()) {
            position = context.getOrganization()
                    .getPositions()
                    .filter(it ->
                            it.getUser().getUsername()
                                    .equals(context.getAccount().getIin() + "-" + ExternalRegistrationPage.CLIENT_LEGAL)
                    ).findAny().orElse(null);
        } else {
            position = context.getOrganization()
                    .getPositions()
                    .filter(it ->
                            it.getUser().getEmail()
                                    .equals(context.getAccount().getEmail())
                    ).findAny().orElse(null);
        }
        context.setPosition(position);
        if (position != null) {
            context.setUser(position.getUser());
        }
    }

    private void searchLegal(DrscbAccountContext context) {
        log.debugf("Searching legal account {} ...", context.getAccount().getId());

        if (context.getAccount().isResident()) {
            context.setOrganization(
                    session.getProvider(OrganizationProvider.class)
                            .getOrganizationByBin(realm, context.getAccount().getBin())
            );
        } else {
            Optional<UserModel> employees = session.users()
                    .searchForUserByUserAttributeStream(
                            realm,
                            DrscbAttributes.LEGAL_NON_RESIDENT_IDN,
                            context.getAccount().getBin()
                    ).findAny();
            if (!employees.isPresent()) {
                return;
            }

            Optional<OrganizationModel> org = session.getProvider(OrganizationProvider.class)
                    .getUserOrganizations(realm, employees.get())
                    .findAny();
            context.setOrganization(org.orElse(null));
        }
    }
}
