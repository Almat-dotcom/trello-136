package kz.kacd.sso.realmcontroller.keycloak;

import kz.kacd.sso.realmcontroller.config.props.RealmControllerProps;
import lombok.RequiredArgsConstructor;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

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
                .password(props.getPassword().trim())
                .resteasyClient(
                        ResteasyClientBuilder.newBuilder()
                                .connectTimeout(2, TimeUnit.SECONDS)
                                .readTimeout(1, TimeUnit.MINUTES)
                                .build()
                )
                .build();
    }
}
