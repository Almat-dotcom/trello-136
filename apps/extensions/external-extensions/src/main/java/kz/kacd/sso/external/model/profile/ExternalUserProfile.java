package kz.kacd.sso.external.model.profile;

import kz.kacd.sso.external.model.profile.validator.ExternalProfileValidator;
import kz.kacd.sso.external.sign.SignatureSubject;
import org.jboss.logging.Logger;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.UserModel;
import org.keycloak.sessions.AuthenticationSessionModel;
import org.keycloak.userprofile.ValidationException;

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

        update();

        return user;
    }

    public void rewriteFromSubject(SignatureSubject subject, AuthenticationSessionModel auth) {
        new ProfileRewriteManager(attributes).rewriteFromSubject(subject, auth);
    }

    public void rewriteFromSession(AuthenticationSessionModel auth) {
        new ProfileRewriteManager(attributes).rewriteFromSession(auth);
    }

    public void update() {
        new ProfileUpdateManager(session, user, attributes).updateUserProfile();
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

    public String eds() {
        return attributes.eds();
    }

    public String orgName() {
        return attributes.orgName();
    }
}
