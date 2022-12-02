package kz.kacd.sso.external.sign.exception;

import kz.kacd.sso.external.model.page.ExternalMessages;

public class InvalidCertificateException extends RuntimeException {

    public InvalidCertificateException() {
        super(ExternalMessages.INVALID_EDS);
    }
}
