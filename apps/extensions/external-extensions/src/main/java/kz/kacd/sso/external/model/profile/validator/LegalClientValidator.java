package kz.kacd.sso.external.model.profile.validator;

import kz.kacd.sso.external.model.OrganizationModel;
import kz.kacd.sso.external.model.OrganizationProvider;
import kz.kacd.sso.external.model.page.ExternalRegistrationPage;
import kz.kacd.sso.external.model.profile.ExternalAttributes;
import kz.kacd.sso.external.model.page.ExternalMessages;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.validate.ValidationError;

import java.util.function.Consumer;

public class LegalClientValidator {
    private static final String VALIDATOR_ID = "external-legal-client-validator";

    private final Consumer<ValidationError> listener;
    private final KeycloakSession session;

    public LegalClientValidator(Consumer<ValidationError> listener, KeycloakSession session) {
        this.listener = listener;
        this.session = session;
    }

    public void validate(ExternalAttributes attributes) {
        if (!attributes.clientType().equals(ExternalRegistrationPage.CLIENT_LEGAL)) {
            return;
        }

        RealmModel realm = session.getContext().getRealm();
        OrganizationProvider orgs = session.getProvider(OrganizationProvider.class);

        OrganizationModel found = orgs.getOrganizationByBin(realm, attributes.bin());

        if (found == null && attributes.legalRole().equals(ExternalRegistrationPage.ROLE_EMPLOYEE)) {
            listener.accept(error(ExternalRegistrationPage.FIELD_BIN, ExternalMessages.ATTEMPT_TO_REGISTER_LEGAL_BY_EMPLOYEE));
        }

    }

    private ValidationError error(String field, String message, Object... args) {
        return new ValidationError(
                VALIDATOR_ID,
                field,
                message,
                args
        );
    }
}
