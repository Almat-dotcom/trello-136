package kz.kacd.sso.external.bmg.exception;

public abstract class MobileVerificationException extends RuntimeException {

    protected MobileVerificationException(String message) {
        super(message);
    }

    protected MobileVerificationException(String message, Throwable cause) {
        super(message, cause);
    }
}
