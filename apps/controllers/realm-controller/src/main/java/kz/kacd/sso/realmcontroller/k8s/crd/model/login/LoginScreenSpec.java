package kz.kacd.sso.realmcontroller.k8s.crd.model.login;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;

@Data
public class LoginScreenSpec {

    @JsonPropertyDescription("Is user registration available")
    private Boolean registration;
    @JsonPropertyDescription("Is forgot password available")
    private Boolean forgotPassword;
    @JsonPropertyDescription("Is remember me available")
    private Boolean rememberMe;
}
