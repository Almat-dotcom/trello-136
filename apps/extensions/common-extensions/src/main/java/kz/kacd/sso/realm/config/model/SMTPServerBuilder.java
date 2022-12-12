package kz.kacd.sso.realm.config.model;

import kz.kacd.sso.k8s.K8sConfig;
import kz.kacd.sso.k8s.secret.Secret;
import kz.kacd.sso.k8s.secret.SecretValueProvider;
import kz.kacd.sso.v1.realmspec.email.Authentication;
import org.keycloak.models.KeycloakSession;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static kz.kacd.sso.util.ValueUtils.defaulted;

public class SMTPServerBuilder {
    private static final String ENCRYPTION_SSL = "ssl";
    private static final String ENCRYPTION_START_TLS = "startTls";

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

    SMTPServerBuilder withFrom(String from, String display) {
        target.put(FROM, defaulted(from, ""));
        target.put(FROM_DISPLAY, defaulted(display, ""));
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
        target.put(SSL, ssl.contains(ENCRYPTION_SSL) ? "true" : "false");
        target.put(START_TLS, ssl.contains(ENCRYPTION_START_TLS) ? "true" : "false");
        return this;
    }

    SMTPServerBuilder withAuth(Authentication spec, KeycloakSession session) {
        if (spec == null) {
            return this;
        }

        Secret secret = null;
        if (spec.getExistingSecret() != null && K8sConfig.ENABLED) {
            secret = session.getProvider(SecretValueProvider.class).findByName(spec.getExistingSecret());
        }

        if (spec.getUsernamePlainValue() != null) {
            target.put(USERNAME, spec.getUsernamePlainValue());
        } else {
            if (secret == null) {
                throw new IllegalStateException("Cannot find secret " + spec.getExistingSecret() + " in k8s!");
            }
            target.put(USERNAME, secret.get(spec.getUsernameKey()));
        }

        if (spec.getPasswordPlainValue() != null) {
            target.put(PASSWORD, spec.getPasswordPlainValue());
        } else {
            if (secret == null) {
                throw new IllegalStateException("Cannot find secret " + spec.getExistingSecret() + " in k8s!");
            }
            target.put(PASSWORD, secret.get(spec.getPasswordKey()));
        }

        target.put(AUTH_ENABLED, "true");
        return this;
    }

    Map<String, String> build() {
        return target;
    }
}
