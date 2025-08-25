package kz.kacd.sso.realm.flow;

public class AuthFlowConstants {

    public static final String RESTRICTED_BROWSER = "RestrictedBrowser";
    public static final String EXTERNAL_LOGIN = "ExternalLogin2FA";
    public static final String EXTERNAL_REGISTRATION = "ExternalRegistration";
    public static final String STANDARD_BROWSER = "browser";
    public static final String STANDARD_REGISTRATION = "registration";
    public static final String COOKIE = "auth-cookie";
    public static final String KERBEROS = "auth-spnego";
    public static final String IDENTITY_PROVIDER = "identity-provider-redirector";
    public static final String STANDARD_USERNAME_FORM = "auth-username-password-form";
    public static final String RESTRICTED_BROWSER_DESC = "Standard browser with restriction to access to specific client";
    public static final String EXTERNAL_LOGIN_DESC = "Extended flow with EDS or password authentication and legal checks";
    public static final String EXTERNAL_REGISTRATION_DESC = "Extended flow to register external user with legals";
    public static final String RESTRICT_CLIENT_ACCESS = "restrict-client-auth-authenticator";
    public static final String RESTRICT_CLIENT_CONFIG = "restricted-access";
    public static final String SESSION_COUNT_LIMIT = "user-session-limits";
    public static final String SESSION_LIMIT_CONFIG = "session-limit";
    public static final String EDS_USERNAME_PASSWORD_FORM = "eds-username-password-form";
    public static final String EXTERNAL_USER_CREATION = "external-user-creation";
    public static final String EXTERNAL_PROFILE = "external-registration-profile-action";

    private AuthFlowConstants() {
    }
}
