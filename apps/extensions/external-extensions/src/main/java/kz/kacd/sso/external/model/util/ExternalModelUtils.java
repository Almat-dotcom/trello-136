package kz.kacd.sso.external.model.util;

import kz.kacd.sso.external.model.page.ExternalRegistrationPage;

import java.net.URLEncoder;

public class ExternalModelUtils {

    private ExternalModelUtils() {
    }

    public static String externalPhysicalResidentUsername(String iin) {
        return iin + "-" + ExternalRegistrationPage.CLIENT_PHYSICAL;
    }

    public static String externalLegalResidentUsername(String iin) {
        return iin + "-" + ExternalRegistrationPage.CLIENT_LEGAL;
    }

    public static String externalNonResidentUsername(String email) {
        return URLEncoder.encode(email);
    }
}
