package kz.kacd.sso.external.sign;

public class SignatureValidator {

    public Result validate(String xmlToVerify) {
        SignedXmlDocument document = new SignedXmlDocument(xmlToVerify);
        document.validate();

        if (document.getError() != null) {
            return new Result(Type.ERROR, null, null, document.getError().getMessage());
        }

        return new Result(
                document.getBin() != null ? Type.LEGAL : Type.PHYSICAL,
                document.getIin(),
                document.getBin(),
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
        private final String iin;
        private final String bin;
        private final String message;

        public Result(Type type, String iin, String bin, String message) {
            this.type = type;
            this.iin = iin;
            this.bin = bin;
            this.message = message;
        }

        public Type getType() {
            return type;
        }

        public String getIin() {
            return iin;
        }

        public String getBin() {
            return bin;
        }

        public String getMessage() {
            return message;
        }
    }
}
