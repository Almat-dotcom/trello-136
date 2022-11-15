package kz.kacd.sso.realmcontroller.keycloak;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.idm.ComponentRepresentation;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.util.MultiValueMapAdapter;
import org.springframework.web.client.RestTemplate;

import javax.ws.rs.core.MultivaluedHashMap;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class ExtendedKeycloak {
    private static final String COMPONENT_SYNC_URL =
            "%/admin/realms/%/user-storage/%/mappers/%/sync?direction=%";
    private static final String DIRECT = "keycloakToFed";
    private static final String REVERSED = "fedToKeycloak";

    private final KeycloakClientFactory factory;
    private final RestTemplate http;
    private final String baseUrl;

    public void sync(String realmName, ComponentRepresentation component, boolean reversed) {
        log.debug("Syncing component {} in realm {} ...", component.getName(), realmName);
        try (var keycloak = factory.create()) {
            var token = keycloak.tokenManager().getAccessTokenString();

            var header = new HashMap<String, List<String>>();
            header.put(HttpHeaders.AUTHORIZATION, List.of("Bearer " + token));
            header.put(HttpHeaders.CONTENT_TYPE, List.of(MediaType.APPLICATION_JSON_VALUE));
            header.put(HttpHeaders.ACCEPT, List.of(MediaType.APPLICATION_JSON_VALUE));
            var entity = new HttpEntity<>("{}", new MultiValueMapAdapter<>(header));

            http.exchange(
                    componentSyncUrl(realmName, component, reversed),
                    HttpMethod.POST,
                    entity,
                    String.class
            );
        }
    }


    private String componentSyncUrl(String realmName, ComponentRepresentation component, boolean reversed) {
        return COMPONENT_SYNC_URL.replaceFirst("%", baseUrl)
                .replaceFirst("%", realmName)
                .replaceFirst("%", component.getParentId())
                .replaceFirst("%", component.getId())
                .replaceFirst("%", reversed ? REVERSED : DIRECT);
    }
}
