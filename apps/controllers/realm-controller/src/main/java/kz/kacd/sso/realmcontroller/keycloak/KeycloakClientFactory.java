package kz.kacd.sso.realmcontroller.keycloak;

import kz.kacd.sso.realmcontroller.config.props.RealmControllerProps;
import lombok.RequiredArgsConstructor;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KeycloakClientFactory {

    private final RealmControllerProps props;

    public Keycloak create() {
        return KeycloakBuilder.builder()
                .serverUrl(props.getKeycloak())
                .grantType(OAuth2Constants.PASSWORD)
                .realm("master")
                .clientId("admin-cli")
                .username(props.getUser())
                .password(props.getPassword())
                .build();
    }
}
