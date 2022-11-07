package kz.kacd.sso.realmcontroller.k8s.crd.model.login;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;

@Data
public class LoginEmailSpec {

    @JsonPropertyDescription("Is email is a valid username")
    private Boolean emailAsUsername;
    @JsonPropertyDescription("Are users allowed to log in with email")
    private Boolean loginWithEmail;
    @JsonPropertyDescription("Is user's emails can be duplicated")
    private Boolean duplicatesEmails;
    @JsonPropertyDescription("Are users forced to verify their emails")
    private Boolean verifyEmail;
}
