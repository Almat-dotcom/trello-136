package kz.kacd.sso.realm.config.model;

import kz.kacd.sso.v1.realmspec.authentication.PasswordPolicy;
import org.jboss.logging.Logger;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class PasswordPolicyBuilder {
    private static final Logger log = Logger.getLogger(PasswordPolicyBuilder.class);

    private static final String UNDEFINED = "undefined";

    private static final String NOT_USERNAME = "notUsername";
    private static final String NOT_EMAIL = "notEmail";
    private static final String EXPIRE_PASSWORD = org.keycloak.models.PasswordPolicy.FORCE_EXPIRED_ID;
    private static final String NOT_REPEATED = org.keycloak.models.PasswordPolicy.PASSWORD_HISTORY_ID;
    private static final String MIN_LENGTH = "length";
    private static final String REGEXP = "regexPattern";
    private static final String SPECIAL_CHARS = "specialChars";
    private static final String UPPERCASE = "upperCase";
    private static final String LOWERCASE = "lowerCase";
    private static final String DIGITS = "digits";
    private static final String BLACKLIST = "passwordBlacklist";

    private static final String BLACKLIST_DIR = "/opt/keycloak/.kacd/blacklist/";

    private final KeycloakSession session;
    private final RealmModel realm;

    public PasswordPolicyBuilder(KeycloakSession session, RealmModel realm) {
        this.session = session;
        this.realm = realm;
    }

    public org.keycloak.models.PasswordPolicy build(PasswordPolicy spec) {
        org.keycloak.models.PasswordPolicy.Builder builder = org.keycloak.models.PasswordPolicy.build();
        builder.put(NOT_USERNAME, UNDEFINED);
        builder.put(NOT_EMAIL, UNDEFINED);

        if (spec == null) {
            return builder.build(session);
        }

        if (spec.getExpiration() != null) {
            builder.put(EXPIRE_PASSWORD, spec.getExpiration().toString());
        }
        if (spec.getNotRecentlyUsed() != null) {
            builder.put(NOT_REPEATED, spec.getNotRecentlyUsed().toString());
        }
        if (spec.getPasswordBlacklist() != null) {
            builder.put(BLACKLIST, writeBlacklist(spec.getPasswordBlacklist()));
        }
        if (spec.getMinimumLength() != null) {
            builder.put(MIN_LENGTH, spec.getMinimumLength().toString());
        }
        if (spec.getRegexp() != null) {
            builder.put(REGEXP, spec.getRegexp());
        }
        if (spec.getCountOfSpecialChars() != null) {
            builder.put(SPECIAL_CHARS, spec.getCountOfSpecialChars().toString());
        }
        if (spec.getUppercaseChars() != null) {
            builder.put(UPPERCASE, spec.getUppercaseChars().toString());
        }
        if (spec.getLowercaseChars() != null) {
            builder.put(LOWERCASE, spec.getLowercaseChars().toString());
        }
        if (spec.getNumberOfDigits() != null) {
            builder.put(DIGITS, spec.getNumberOfDigits().toString());
        }
        return builder.build(session);
    }

    private String writeBlacklist(String content) {
        try {
            if (!new File(BLACKLIST_DIR).mkdirs()) {
                throw new IllegalStateException("Cannot create blacklist dir!");
            }
            File file = new File(BLACKLIST_DIR + realm.getName());
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(content);
            }
            return file.getAbsolutePath();
        } catch (Exception e) {
            log.errorf("Error on writing password blacklist!", e);
            throw new IllegalStateException(e);
        }
    }
}
