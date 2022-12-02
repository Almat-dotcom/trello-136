package kz.kacd.sso.external.sign.exception;

import kz.kacd.sso.external.model.page.ExternalMessages;

public class CertificateNotFoundException extends RuntimeException {

    public CertificateNotFoundException() {
        super(ExternalMessages.INVALID_EDS);
    }
}
