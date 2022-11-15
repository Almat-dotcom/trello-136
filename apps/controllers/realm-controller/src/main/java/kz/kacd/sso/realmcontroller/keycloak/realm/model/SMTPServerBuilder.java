package kz.kacd.sso.realmcontroller.keycloak.realm.model;

import kz.kacd.sso.realmcontroller.k8s.crd.realm.model.email.EmailAuthSpec;
import kz.kacd.sso.realmcontroller.k8s.crd.realm.model.email.EmailSpec;
import kz.kacd.sso.realmcontroller.k8s.model.SecretData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static kz.kacd.sso.realmcontroller.util.ValueUtils.defaulted;

public class SMTPServerBuilder {
    private static final String FROM = "from";
    private static final String FROM_DISPLAY = "fromDisplayName";
    private static final String SMTP_HOST = "host";
    private static final String SMTP_PORT = "port";
    private static final String SSL = "ssl";
    private static final String START_TLS = "starttls";
    private static final String AUTH_ENABLED = "auth";
    private static final String USERNAME = "user";
    private static final String PASSWORD = "password";

    private final Map<String, String> target = new HashMap<>();

    SMTPServerBuilder withFrom(String from) {
        target.put(FROM, defaulted(from, ""));
        target.put(FROM_DISPLAY, defaulted(from.split("@")[0].toUpperCase(), ""));
        return this;
    }

    SMTPServerBuilder withHostAndPort(String host, Integer port) {
        target.put(SMTP_HOST, defaulted(host, ""));
        if (port != null) {
            target.put(SMTP_PORT, port.toString());
        } else {
            target.put(SMTP_PORT, "");
        }
        return this;
    }

    SMTPServerBuilder withSsl(List<String> ssl) {
        target.put(SSL, ssl.contains(EmailSpec.ENCRYPTION_SSL) ? "true" : "false");
        target.put(START_TLS, ssl.contains(EmailSpec.ENCRYPTION_START_TLS) ? "true" : "false");
        return this;
    }

    SMTPServerBuilder withAuth(EmailAuthSpec spec, Map<String, SecretData> secrets) {
        if (spec == null) {
            return this;
        }

        if (!secrets.containsKey(spec.getExistingSecret())) {
            throw new IllegalStateException("Invalid email.existingSecret value! Secret not found!");
        }

        var secret = secrets.get(spec.getExistingSecret());
        target.put(AUTH_ENABLED, "true");
        target.put(USERNAME, secret.get(spec.getUsernameKey()));
        target.put(PASSWORD, secret.get(spec.getPasswordKey()));
        return this;
    }

    Map<String, String> build() {
        return target;
    }
}
