package kz.kacd.sso.realm.config.model;

import kz.kacd.sso.v1.realmspec.security.Headers;

import java.util.HashMap;
import java.util.Map;

import static kz.kacd.sso.util.ValueUtils.defaulted;

public class ContentPolicyBuilder {

    private static final String CONTENT_TYPE_OPTIONS = "_browser_header.xContentTypeOptions";
    private static final String ROBOTS = "_browser_header.xRobotsTag";
    private static final String FRAME_OPTIONS = "_browser_header.xFrameOptions";
    private static final String CONTENT_SECURITY_POLICY = "_browser_header.contentSecurityPolicy";
    private static final String XSS = "_browser_header.xXSSProtection";
    private static final String STRICT_TRANSPORT_SECURITY = "_browser_header.strictTransportSecurity";

    private final Map<String, String> target = new HashMap<>();

    public Map<String, String> buildFrom(Headers source) {
        target.put(CONTENT_TYPE_OPTIONS, defaulted(source.getContentTypeOptions(), "nosniff"));
        target.put(ROBOTS, defaulted(source.getRobotsTag(), "none"));
        target.put(FRAME_OPTIONS, defaulted(source.getFrameOptions(), "SAMEORIGIN"));
        target.put(
                CONTENT_SECURITY_POLICY,
                defaulted(
                        source.getCsp(),
                        "frame-src 'self'; frame-ancestors 'self'; object-src 'none';"
                )
        );
        target.put(XSS, defaulted(source.getXss(), "1; mode=block"));
        target.put(STRICT_TRANSPORT_SECURITY, defaulted(source.getHsts(), "max-age=31536000; includeSubDomains"));
        return target;
    }
}
