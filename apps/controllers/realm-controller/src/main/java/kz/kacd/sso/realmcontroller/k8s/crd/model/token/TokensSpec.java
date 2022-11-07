package kz.kacd.sso.realmcontroller.k8s.crd.model.token;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;

@Data
public class TokensSpec {

    @JsonPropertyDescription(
            "lifetime of the access token (It is recommended for this value to be shorter than the SSO session idle timeout: 30 minutes)"
    )
    private String accessLifespan;
    @JsonPropertyDescription("OIDC implicid flow access token lifetime")
    private String oidcAccessLifespan;
}
