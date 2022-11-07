package kz.kacd.sso.realmcontroller.k8s.crd.model.themes;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;

@Data
public class ThemesSpec {

    @JsonPropertyDescription("Theme of login page")
    private String login;
    @JsonPropertyDescription("Theme of the account page")
    private String account;
    @JsonPropertyDescription("Theme of the admin console page")
    private String admin;
    @JsonPropertyDescription("Theme of the email")
    private String email;
}
