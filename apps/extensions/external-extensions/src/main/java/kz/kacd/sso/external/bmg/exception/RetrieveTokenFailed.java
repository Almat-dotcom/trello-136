package kz.kacd.sso.external.bmg.exception;

public class RetrieveTokenFailed extends MobileVerificationException {
    public RetrieveTokenFailed(String message) {
        super(message);
    }

    public RetrieveTokenFailed(String message, Throwable cause) {
        super(message, cause);
    }
}
