package kz.kacd.sso.realmcontroller.k8s.crd.realm.model.event;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;

import java.util.List;

@Data
public class UserEventsSpec {
    public static final List<String> DEFAULT_EVENTS = List.of(
            "UPDATE_CONSENT_ERROR",
            "SEND_RESET_PASSWORD",
            "GRANT_CONSENT",
            "VERIFY_PROFILE_ERROR",
            "UPDATE_TOTP",
            "REMOVE_TOTP",
            "REVOKE_GRANT",
            "LOGIN_ERROR",
            "CLIENT_LOGIN",
            "RESET_PASSWORD_ERROR",
            "IMPERSONATE_ERROR",
            "CODE_TO_TOKEN_ERROR",
            "CUSTOM_REQUIRED_ACTION",
            "OAUTH2_DEVICE_CODE_TO_TOKEN_ERROR",
            "RESTART_AUTHENTICATION",
            "UPDATE_PROFILE_ERROR",
            "IMPERSONATE",
            "LOGIN",
            "UPDATE_PASSWORD_ERROR",
            "OAUTH2_DEVICE_VERIFY_USER_CODE",
            "CLIENT_INITIATED_ACCOUNT_LINKING",
            "TOKEN_EXCHANGE",
            "REGISTER",
            "LOGOUT",
            "AUTHREQID_TO_TOKEN",
            "DELETE_ACCOUNT_ERROR",
            "CLIENT_REGISTER",
            "IDENTITY_PROVIDER_LINK_ACCOUNT",
            "UPDATE_PASSWORD",
            "DELETE_ACCOUNT",
            "FEDERATED_IDENTITY_LINK_ERROR",
            "CLIENT_DELETE",
            "IDENTITY_PROVIDER_FIRST_LOGIN",
            "VERIFY_EMAIL",
            "CLIENT_DELETE_ERROR",
            "CLIENT_LOGIN_ERROR",
            "RESTART_AUTHENTICATION_ERROR",
            "REMOVE_FEDERATED_IDENTITY_ERROR",
            "EXECUTE_ACTIONS",
            "TOKEN_EXCHANGE_ERROR",
            "PERMISSION_TOKEN",
            "SEND_IDENTITY_PROVIDER_LINK_ERROR",
            "EXECUTE_ACTION_TOKEN_ERROR",
            "SEND_VERIFY_EMAIL",
            "OAUTH2_DEVICE_AUTH",
            "EXECUTE_ACTIONS_ERROR",
            "REMOVE_FEDERATED_IDENTITY",
            "OAUTH2_DEVICE_CODE_TO_TOKEN",
            "IDENTITY_PROVIDER_POST_LOGIN",
            "IDENTITY_PROVIDER_LINK_ACCOUNT_ERROR",
            "UPDATE_EMAIL",
            "OAUTH2_DEVICE_VERIFY_USER_CODE_ERROR",
            "REGISTER_ERROR",
            "REVOKE_GRANT_ERROR",
            "LOGOUT_ERROR",
            "UPDATE_EMAIL_ERROR",
            "EXECUTE_ACTION_TOKEN",
            "CLIENT_UPDATE_ERROR",
            "UPDATE_PROFILE",
            "AUTHREQID_TO_TOKEN_ERROR",
            "FEDERATED_IDENTITY_LINK",
            "CLIENT_REGISTER_ERROR",
            "SEND_VERIFY_EMAIL_ERROR",
            "SEND_IDENTITY_PROVIDER_LINK",
            "RESET_PASSWORD",
            "CLIENT_INITIATED_ACCOUNT_LINKING_ERROR",
            "OAUTH2_DEVICE_AUTH_ERROR",
            "UPDATE_CONSENT",
            "REMOVE_TOTP_ERROR",
            "VERIFY_EMAIL_ERROR",
            "SEND_RESET_PASSWORD_ERROR",
            "CLIENT_UPDATE",
            "IDENTITY_PROVIDER_POST_LOGIN_ERROR",
            "CUSTOM_REQUIRED_ACTION_ERROR",
            "UPDATE_TOTP_ERROR",
            "CODE_TO_TOKEN",
            "VERIFY_PROFILE",
            "GRANT_CONSENT_ERROR",
            "IDENTITY_PROVIDER_FIRST_LOGIN_ERROR"
    );

    @JsonPropertyDescription("If true user events will be enabled")
    private Boolean saveEvents;
    @JsonPropertyDescription("Duration of user events persist time (duration can be m(Minutest), h(Hours), d(Days))")
    private String expiration;
    @JsonPropertyDescription(
            "List of event types which will be saved. If empty and events active, it creates standard list of types."
    )
    private List<String> savedTypes;
}
