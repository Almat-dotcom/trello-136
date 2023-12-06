package kz.kacd.sso.external.model.profile;

import kz.kacd.sso.external.model.OrganizationModel;
import kz.kacd.sso.external.model.OrganizationProvider;
import kz.kacd.sso.external.model.PositionModel;
import kz.kacd.sso.external.model.page.ExternalRegistrationPage;
import org.jboss.logging.Logger;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

public class ProfileUpdateManager {
    private static final Logger log = Logger.getLogger(ProfileUpdateManager.class);

    private final KeycloakSession session;
    private final UserModel user;
    private final ExternalAttributes attributes;

    public ProfileUpdateManager(KeycloakSession session, UserModel user, ExternalAttributes attributes) {
        this.session = session;
        this.user = user;
        this.attributes = attributes;
    }

    void updateUserProfile() {
        log.debug("Updating user attributes ...");
        if (user == null) {
            throw new IllegalStateException("Attempt to update user profile without user initialization!");
        }

        if (attributes.email() != null) {
            user.setEmail(attributes.email());
        }
        if (attributes.firstName() != null) {
            user.setFirstName(attributes.firstName());
        }
        if (attributes.lastName() != null) {
            user.setLastName(attributes.lastName());
        }
        if (attributes.clientType() != null) {
            user.setSingleAttribute(ExternalRegistrationPage.FIELD_CLIENT_TYPE, attributes.clientType());
        }
        if (attributes.middleName() != null) {
            user.setSingleAttribute(ExternalRegistrationPage.FIELD_MIDDLE_NAME, attributes.middleName());
        }
        if (attributes.iin() != null) {
            user.setSingleAttribute(ExternalRegistrationPage.FIELD_IIN, attributes.iin());
        }
        if (attributes.residency() != null) {
            user.setSingleAttribute(ExternalRegistrationPage.FIELD_RESIDENCY, attributes.residency());
        }
        if (attributes.phoneNumber() != null) {
            user.setSingleAttribute(ExternalRegistrationPage.FIELD_PHONE_NUMBER, attributes.phoneNumber());
        }
        if (!user.getAttributes().containsKey(ExternalRegistrationPage.FIELD_PHONE_VERIFIED)) {
            user.setSingleAttribute(ExternalRegistrationPage.FIELD_PHONE_VERIFIED, Boolean.FALSE.toString());
        }
        if (attributes.locale() != null) {
            user.setSingleAttribute(ExternalRegistrationPage.FIELD_LOCALE, attributes.locale());
        }

        if (
                ExternalRegistrationPage.CLIENT_LEGAL.equals(attributes.clientType())
                || ExternalRegistrationPage.CLIENT_LEGAL.equals(user.getFirstAttribute(ExternalRegistrationPage.FIELD_CLIENT_TYPE))
        ) {
            processLegalClient();
        }
    }

    private void processLegalClient() {
        log.debugf("Processing legal client profile ...");
        OrganizationModel org = findOrCreateOrg();
        PositionModel head = org.getHead();
        if (head != null && head.getUser().getFirstAttribute(ExternalRegistrationPage.FIELD_HAS_EBR) != null) {
            user.setSingleAttribute(
                    ExternalRegistrationPage.FIELD_HAS_EBR,
                    head.getUser().getFirstAttribute(ExternalRegistrationPage.FIELD_HAS_EBR)
            );
        }
        applyOrgAttributes(org);
        updatePositions(org);
    }

    private OrganizationModel findOrCreateOrg() {
        OrganizationModel result = findOrg();
        if (result == null) {
            log.debugf("Creating new organization head %s ...", user.getId());
            result = session.getProvider(OrganizationProvider.class)
                    .createOrganization(session.getContext().getRealm(), user);
        }
        return result;
    }

    private OrganizationModel findOrg() {
        RealmModel realm = session.getContext().getRealm();
        OrganizationProvider provider = session.getProvider(OrganizationProvider.class);
        OrganizationModel org;
        if (resident() || nonResidentEmployee()) {
            org = provider.getOrganizationByBin(realm, attributes.bin());
        } else {
            org = provider.getUserOrganizations(realm, user).findAny().orElse(null);
        }
        if (
                org == null
                && !ExternalRegistrationPage.RESIDENT.equals(attributes.residency())
                && !ExternalRegistrationPage.ROLE_HEAD.equals(attributes.legalRole())
        ) {
            throw new IllegalStateException(
                    "Illegal organization creation request! Only head of company can register new legal!"
            );
        }
        return org;
    }

    private void applyOrgAttributes(OrganizationModel org) {
        if (ExternalRegistrationPage.RESIDENT.equals(attributes.residency())) {
            org.setBin(attributes.bin());
            org.setName(attributes.orgName());
            org.setDisplayName(attributes.orgName());
        } else if (org.getBin() == null) {
            org.setBin(session.getProvider(OrganizationProvider.class).generateNonResidentOrganizationBin());
        }
    }

    private void updatePositions(OrganizationModel org) {
        PositionModel position = org.getPosition(user);
        if (position == null) {
            createNewPosition(org);
        } else {
            updatePosition(org, position);
        }
    }

    private void createNewPosition(OrganizationModel org) {
        if (!resident() && ExternalRegistrationPage.ROLE_HEAD.equals(attributes.legalRole())) {
            throw new IllegalStateException(
                    "Cannot create new HEAD position for org " + org.getBin() + " because it is not resident!"
            );
        }

        log.debugf("Creating new position for user %s in org %s ...", user.getId(), org.getBin());
        if (ExternalRegistrationPage.ROLE_HEAD.equals(attributes.legalRole())) {
            log.infof("Changing HEAD position in org %s to user %s ...", org.getBin(), user.getId());
            removeExistingHead(org);
            PositionModel newHead = org.requestPosition(PositionModel.HEAD, user);
            org.confirmPosition(newHead);
            return;
        }

        log.infof("Requesting new EMPLOYEE position for user %s in org %s ...", user.getId(), org.getBin());
        org.requestPosition(PositionModel.EMPLOYEE, user);
    }

    private void updatePosition(OrganizationModel org, PositionModel position) {
        if (!resident() || attributes.legalRole().equalsIgnoreCase(position.getName())) {
            return;
        }

        if (ExternalRegistrationPage.ROLE_EMPLOYEE.equals(attributes.legalRole())) {
            log.infof("Changing position from HEAD to EMPLOYEE for user %s in org %s ...", user.getId(), org.getBin());
            org.removePosition(position);
            org.requestPosition(PositionModel.EMPLOYEE, user);
            return;
        }

        if (ExternalRegistrationPage.ROLE_HEAD.equals(attributes.legalRole())) {
            removeExistingHead(org);
            log.infof("Changing position from EMPLOYEE to HEAD for user %s in org %s ...", user.getId(), org.getBin());
            org.removePosition(position);
            PositionModel newHead = org.requestPosition(PositionModel.HEAD, user);
            org.confirmPosition(newHead);
        }
    }

    private void removeExistingHead(OrganizationModel org) {
        PositionModel existingHead = findExistingHead(org);
        if (existingHead == null || existingHead.getUser().getId().equals(user.getId())) {
            return;
        }

        UserModel oldHeadUser = existingHead.getUser();
        log.infof("Changing position for user %s in org %s from HEAD to EMPLOYEE ...", oldHeadUser.getId(), org.getBin());
        org.removePosition(existingHead);
        org.requestPosition(PositionModel.EMPLOYEE, oldHeadUser);
    }

    private PositionModel findExistingHead(OrganizationModel org) {
        return org.getPositions().filter(it -> ExternalRegistrationPage.ROLE_HEAD.equalsIgnoreCase(it.getName()))
                .findFirst().orElse(null);
    }

    private boolean resident() {
        if (attributes.residency() == null && user != null) {
            return ExternalRegistrationPage.RESIDENT.equals(user.getFirstAttribute(ExternalRegistrationPage.FIELD_RESIDENCY));
        }
        return attributes.residency() != null && attributes.residency().equals(ExternalRegistrationPage.RESIDENT);
    }

    private boolean nonResidentEmployee() {
        return attributes.residency() != null
               && attributes.residency().equals(ExternalRegistrationPage.NON_RESIDENT)
               && attributes.clientType().equals(ExternalRegistrationPage.CLIENT_LEGAL)
               && attributes.legalRole() != null
               && attributes.legalRole().equals(ExternalRegistrationPage.ROLE_EMPLOYEE);
    }
}
