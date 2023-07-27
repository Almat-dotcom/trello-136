package kz.kacd.sso.external.sign;

public class SignatureValidator {

    public Result validate(String xmlToVerify) {
        SignedXmlDocument document = new SignedXmlDocument(xmlToVerify);
        document.validate();

        if (document.getError() != null) {
            return new Result(Type.ERROR, null, document.getError().getMessage());
        }

        return new Result(
                document.getSubject().legal() ? Type.LEGAL : Type.PHYSICAL,
                document.getSubject(),
                null
        );
    }

    public enum Type {
        ERROR,
        PHYSICAL,
        LEGAL
    }

    public static final class Result {

        private final Type type;
        private final SignatureSubject subject;
        private final String message;

        public Result(Type type, SignatureSubject subject, String message) {
            this.type = type;
            this.subject = subject;
            this.message = message;
        }

        public Type getType() {
            return type;
        }

        public SignatureSubject getSubject() {
            return subject;
        }

        public String getMessage() {
            return message;
        }
    }
}
