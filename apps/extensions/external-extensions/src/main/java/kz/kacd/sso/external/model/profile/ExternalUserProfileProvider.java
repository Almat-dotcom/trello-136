package kz.kacd.sso.external.model.profile;

import org.keycloak.models.KeycloakSession;
import org.keycloak.models.UserModel;

import jakarta.ws.rs.core.MultivaluedMap;
import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ExternalUserProfileProvider {

    private final KeycloakSession session;

    public ExternalUserProfileProvider(KeycloakSession session) {
        this.session = session;
    }

    public ExternalUserProfile create(MultivaluedMap<String, String> formData) {
        return new ExternalUserProfile(new ExternalAttributes(toSimpleMap(formData)), session, null);
    }

    public ExternalUserProfile create(MultivaluedMap<String, String> formData, UserModel user) {
        return new ExternalUserProfile(new ExternalAttributes(toSimpleMap(formData)), session, user);
    }

    private Map<String, String> toSimpleMap(MultivaluedMap<String, String> source) {
        return source.entrySet().stream().map(it ->
                new AbstractMap.SimpleEntry<>(it.getKey(), it.getValue().get(0))
        ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
