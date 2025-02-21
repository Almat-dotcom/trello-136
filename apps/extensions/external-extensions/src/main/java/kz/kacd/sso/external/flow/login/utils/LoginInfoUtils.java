package kz.kacd.sso.external.flow.login.utils;

import java.time.ZoneOffset;

public class LoginInfoUtils {
    public static final String LAST_LOGIN_TIME = "lastLoginTime";
    public static final String LAST_LOGIN_IP = "lastLoginIP";
    public static final String LAST_LOGIN_OS = "lastLoginOS";
    public static final String LAST_LOGIN_BROWSER = "lastLoginBrowser";

    public static final String PREVIOUS_LOGIN_TIME = "previousLoginTime";
    public static final String PREVIOUS_LOGIN_IP = "previousLoginIP";
    public static final String PREVIOUS_LOGIN_OS = "previousLoginOS";
    public static final String PREVIOUS_LOGIN_BROWSER = "previousLoginBrowser";

    public static final String UNKNOWN_BROWSER = "Unknown Browser";
    public static final String UNKNOWN_OS = "Unknown OS";
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    public static final ZoneOffset TIME_ZONE_OFFSET = ZoneOffset.ofHours(5);
    public static final String HEADER_USER_AGENT = "User-Agent";
    public static final String HEADER_X_FORWARDED_FOR = "X-Forwarded-For";
}
