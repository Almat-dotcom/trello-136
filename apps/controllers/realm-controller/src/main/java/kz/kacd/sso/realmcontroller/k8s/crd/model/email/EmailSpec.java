package kz.kacd.sso.realmcontroller.k8s.crd.model.email;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;

import java.util.List;

@Data
public class EmailSpec {
    public static final String ENCRYPTION_SSL = "ssl";
    public static final String ENCRYPTION_START_TLS = "startTls";

    @JsonPropertyDescription("Email address of the sender")
    private String from;
    @JsonPropertyDescription("Host of smtp server")
    private String smtpHost;
    @JsonPropertyDescription("Port of smtp server")
    private Integer smtpPort;
    @JsonPropertyDescription("Mode of encryption")
    private List<String> encryption;
    @JsonPropertyDescription("authentication settings")
    private EmailAuthSpec authentication;
}
