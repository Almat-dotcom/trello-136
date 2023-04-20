package kz.kacd.sso.sign.exception;

import kz.kacd.sso.login.ExtensionMessages;

public class InvalidSignatureException extends RuntimeException {

    public InvalidSignatureException() {
        super(ExtensionMessages.INVALID_EDS);
    }
}
