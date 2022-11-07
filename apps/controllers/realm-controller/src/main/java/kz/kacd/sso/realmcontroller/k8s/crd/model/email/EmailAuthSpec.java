package kz.kacd.sso.realmcontroller.k8s.crd.model.email;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;

@Data
public class EmailAuthSpec {

    @JsonPropertyDescription("Name of the secret with authentication credentials")
    private String existingSecret;
    @JsonPropertyDescription("Name of the key where username is stored")
    private String usernameKey;
    @JsonPropertyDescription("Name of the key where password is stored")
    private String passwordKey;
}
