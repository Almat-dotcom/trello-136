package kz.kacd.sso.external.representation;

public class ClientSecretRepresentation {

    private final String clientSecret;

    public ClientSecretRepresentation(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getClientSecret() {
        return clientSecret;
    }
}
