package kz.kacd.sso.external.bmg.event;

import kz.kacd.sso.external.bmg.MobilePhoneValidator;
import kz.kacd.sso.external.model.page.ExternalRegistrationPage;
import org.jboss.logging.Logger;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.events.admin.OperationType;
import org.keycloak.events.admin.ResourceType;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.UserModel;

public class PhoneVerifierListener implements EventListenerProvider {
    private static final Logger log = Logger.getLogger(PhoneVerifierListener.class);

    private final KeycloakSession session;

    public PhoneVerifierListener(KeycloakSession session) {
        this.session = session;
    }

    @Override
    public void onEvent(Event event) {
        // Nothing to do
    }

    @Override
    public void onEvent(AdminEvent event, boolean includeRepresentation) {
        if (
                event.getResourceType().equals(ResourceType.USER)
                        && event.getOperationType().equals(OperationType.UPDATE)
        ) {
            checkPhoneNumber(event.getResourcePath().replace("users/", ""));
        }
    }

    private void checkPhoneNumber(String userId) {
        UserModel user = session.users().getUserById(session.getContext().getRealm(), userId);
        if (user == null) {
            log.warnf("Cannot find user %s in current realm %s!", userId, session.getContext().getRealm().getName());
            return;
        }

        if (residentPhysical(user) && requiredAttributesExists(user)) {
            String iin = extractIin(user);
            String phoneNumber = extractPhone(user);
            MobilePhoneValidator validator = session.getProvider(MobilePhoneValidator.class);
            boolean result = validator.check(iin, phoneNumber);
            user.setSingleAttribute(ExternalRegistrationPage.FIELD_PHONE_VERIFIED, result + "");
        }
    }

    private String extractPhone(UserModel user) {
        return user.getFirstAttribute(ExternalRegistrationPage.FIELD_PHONE_NUMBER);
    }

    private String extractIin(UserModel user) {
        return user.getFirstAttribute(ExternalRegistrationPage.FIELD_IIN);
    }

    private boolean requiredAttributesExists(UserModel user) {
        return user.getFirstAttribute(ExternalRegistrationPage.FIELD_IIN) != null
                && user.getFirstAttribute(ExternalRegistrationPage.FIELD_PHONE_NUMBER) != null;
    }

    private boolean residentPhysical(UserModel user) {
        return ExternalRegistrationPage.RESIDENT.equals(user.getFirstAttribute(ExternalRegistrationPage.FIELD_RESIDENCY))
                && ExternalRegistrationPage.CLIENT_PHYSICAL.equals(user.getFirstAttribute(ExternalRegistrationPage.FIELD_CLIENT_TYPE));
    }

    @Override
    public void close() {
        // Nothing to close
    }
}
