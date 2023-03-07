package kz.kacd.sso.external.model.profile;

import kz.kacd.sso.external.model.OrganizationModel;
import kz.kacd.sso.external.model.OrganizationProvider;
import kz.kacd.sso.external.model.PositionModel;
import kz.kacd.sso.external.model.page.ExternalRegistrationPage;
import kz.kacd.sso.external.model.profile.validator.ExternalProfileValidator;
import org.jboss.logging.Logger;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.userprofile.ValidationException;

import java.util.Collections;

public class ExternalUserProfile {
    private static final Logger log = Logger.getLogger(ExternalUserProfile.class);

    private final ExternalAttributes attributes;
    private final KeycloakSession session;
    private UserModel user;

    public ExternalUserProfile(ExternalAttributes attributes, KeycloakSession session, UserModel user) {
        this.attributes = attributes;
        this.session = session;
        this.user = user;
    }

    public ValidationException validate() {
        return new ExternalProfileValidator(session).validate(attributes);
    }

    public UserModel create() {
        if (user != null) {
            return user;
        }

        log.debugf("Creating new user profile %s ...", username());
        user = session.users().addUser(session.getContext().getRealm(), username());

        updateUserAttributes();

        return user;
    }

    public void update() {
        updateUserAttributes();
    }

    private void updateUserAttributes() {
        log.debug("Updating user attributes ...");
        if (user == null) {
            throw new IllegalStateException("Attempt to update user profile without user initialization!");
        }

        user.setEmail(email());
        user.setFirstName(firstName());
        user.setLastName(lastName());
        user.setAttribute(ExternalRegistrationPage.FIELD_CLIENT_TYPE, Collections.singletonList(clientType()));
        user.setAttribute(ExternalRegistrationPage.FIELD_MIDDLE_NAME, Collections.singletonList(middleName()));
        user.setAttribute(ExternalRegistrationPage.FIELD_IIN, Collections.singletonList(iin()));
        user.setAttribute(ExternalRegistrationPage.FIELD_RESIDENCY, Collections.singletonList(residency()));
        user.setAttribute(ExternalRegistrationPage.FIELD_PHONE_NUMBER, Collections.singletonList(phoneNumber()));
        user.setAttribute(ExternalRegistrationPage.FIELD_PHONE_VERIFIED, Collections.singletonList(Boolean.FALSE.toString()));
        if (locale() != null) {
            user.setAttribute(ExternalRegistrationPage.FIELD_LOCALE, Collections.singletonList(locale()));
        }

        if (ExternalRegistrationPage.CLIENT_LEGAL.equals(clientType())) {
            processLegalClient();
        }
    }

    private void processLegalClient() {
        log.debug("Processing legal client profile ...");
        RealmModel realm = session.getContext().getRealm();

        OrganizationProvider provider = session.getProvider(OrganizationProvider.class);
        OrganizationModel org;
        if (resident() || nonResidentEmployee()) {
            org = provider.getOrganizationByBin(realm, bin());
        } else {
            org = provider.getUserOrganizations(realm, user).findAny().orElse(null);
        }
        if (org == null && !ExternalRegistrationPage.ROLE_HEAD.equals(legalRole())) {
            throw new IllegalStateException(
                    "Illegal organization creation request! Only head of company can register new legal!"
            );
        }

        if (org == null) {
            log.debugf("Registering new organization %s with user %s ...", bin(), username());
            org = provider.createOrganization(realm, user);

            if (residency().equals(ExternalRegistrationPage.NON_RESIDENT)) {
                org.setBin(provider.generateNonResidentOrganizationBin());
            } else {
                org.setBin(bin());
            }
            return;
        }

        PositionModel position = org.getPosition(user);
        if (position != null) {
            log.debugf("User %s has already requested membership in organization %s!", username(), bin());
            return;
        }

        log.debugf("Requesting employee position for user %s in org %s ...", username(), bin());
        org.requestPosition(PositionModel.EMPLOYEE, user);
    }

    private boolean resident() {
        return residency() != null && residency().equals(ExternalRegistrationPage.RESIDENT);
    }

    private boolean nonResidentEmployee() {
        return residency() != null
                && residency().equals(ExternalRegistrationPage.NON_RESIDENT)
                && clientType().equals(ExternalRegistrationPage.CLIENT_LEGAL)
                && legalRole() != null
                && legalRole().equals(ExternalRegistrationPage.ROLE_EMPLOYEE);
    }

    public String email() {
        return attributes.email();
    }

    public String username() {
        return attributes.username();
    }

    public String iin() {
        return attributes.iin();
    }

    public String firstName() {
        return attributes.firstName();
    }

    public String lastName() {
        return attributes.lastName();
    }

    public String middleName() {
        return attributes.middleName();
    }

    public String clientType() {
        return attributes.clientType();
    }

    public String bin() {
        return attributes.bin();
    }

    public String legalRole() {
        return attributes.legalRole();
    }

    public String residency() {
        return attributes.residency();
    }

    public String locale() {
        return attributes.locale();
    }

    public String phoneNumber() {
        return attributes.phoneNumber();
    }
}
