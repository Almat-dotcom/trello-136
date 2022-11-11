package kz.kacd.sso.realmcontroller.k8s.crd.realm.model.login;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;

@Data
public class LoginSpec {

    @JsonPropertyDescription("Login screen config")
    private LoginScreenSpec loginScreen;
    @JsonPropertyDescription("email settings")
    private LoginEmailSpec email;
    @JsonPropertyDescription("User info settings")
    private LoginUserInfoSpec info;
}
