package kz.kacd.sso.realmcontroller.k8s.crd.realm.model;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import kz.kacd.sso.realmcontroller.k8s.crd.realm.model.email.EmailSpec;
import kz.kacd.sso.realmcontroller.k8s.crd.realm.model.event.EventsSpec;
import kz.kacd.sso.realmcontroller.k8s.crd.realm.model.federation.FederationSpec;
import kz.kacd.sso.realmcontroller.k8s.crd.realm.model.localization.LocalizationSpec;
import kz.kacd.sso.realmcontroller.k8s.crd.realm.model.login.LoginSpec;
import kz.kacd.sso.realmcontroller.k8s.crd.realm.model.security.SecuritySpec;
import kz.kacd.sso.realmcontroller.k8s.crd.realm.model.session.SessionsSpec;
import kz.kacd.sso.realmcontroller.k8s.crd.realm.model.themes.ThemesSpec;
import kz.kacd.sso.realmcontroller.k8s.crd.realm.model.token.TokensSpec;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RealmSpec {
    public static final String SSL_ALL = "all";
    public static final String SSL_EXTERNAL = "external";
    public static final String SSL_NONE = "none";

    @JsonPropertyDescription("Displayed name of the realm")
    private String displayedName;
    @JsonPropertyDescription("Host for specific realm")
    private String frontendUrl;
    @JsonPropertyDescription("Mode of the ssl requirement")
    private String requireSsl;
    @JsonPropertyDescription("configuration of the login")
    private LoginSpec login;
    @JsonPropertyDescription("Email options")
    private EmailSpec email;
    @JsonPropertyDescription("Specify themes for realm")
    private ThemesSpec themes;
    @JsonPropertyDescription("Configuration of user events storing")
    private EventsSpec events;
    @JsonPropertyDescription("Localization settings")
    private LocalizationSpec localization;
    @JsonPropertyDescription("Config of additional security")
    private SecuritySpec security;
    @JsonPropertyDescription("Sessions config")
    private SessionsSpec sessions;
    @JsonPropertyDescription("Settings of tokens")
    private TokensSpec tokens;
    @JsonPropertyDescription("Settings of user federations")
    private FederationSpec federations;
}
