package kz.kacd.sso.external.bmg.exception;

public class IOFailed extends MobileVerificationException {
    public IOFailed(String message, Throwable cause) {
        super(message, cause);
    }
}
