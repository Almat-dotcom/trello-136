package kz.kacd.sso.realmcontroller.k8s.crd.client.model.access;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;

import java.util.List;

@Data
public class ClientAccessSpec {

    @JsonPropertyDescription("Root url of the client")
    private String rootUrl;
    @JsonPropertyDescription(
            "Home path of the client (this link will be used when keycloak needs to link client home page). " +
                    "If you already have specified rootUrl, you need just specify path after rootUrl to visit home page."
    )
    private String homeUrl;
    @JsonPropertyDescription(
            "List of valid redirect uris. Redirect URI-s are URI-s which can be used by client to log in user. " +
                    "If you use rootUrl, you can just specify path after rootUrl. " +
                    "You can use wildcard patterns. E.g. https://myapp/app1/*"
    )
    private List<String> validRedirectUris;
    @JsonPropertyDescription(
            "List of valid redirect uris after successful logout. If you want to use the same uris as validRedirectUris" +
                    " specify single '+' valued in this array."
    )
    private List<String> validLogoutRedirectUris;
    @JsonPropertyDescription(
            "List of web origins which will be permitted to access keycloak. It specifies CORS policies." +
                    " You can just include single element '+' to include in this list all origins from validRedirectUris."
    )
    private List<String> webOrigins;
}
