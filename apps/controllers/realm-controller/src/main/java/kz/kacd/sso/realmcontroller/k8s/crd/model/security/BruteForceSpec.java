package kz.kacd.sso.realmcontroller.k8s.crd.model.security;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;

@Data
public class BruteForceSpec {

    @JsonPropertyDescription("If true Brute force attempts will be detected")
    private Boolean enabled;
    @JsonPropertyDescription("Count of login failures before lockout")
    private Integer maxLoginFailures;
    @JsonPropertyDescription("If true after brute force detection user will be locked permanently")
    private Boolean permanentLockout;
    @JsonPropertyDescription("How much time user should be locked out (duration can be m(Minutest), h(Hours), d(Days))")
    private String waitIncrement;
    @JsonPropertyDescription("Maximum time of user lockout (duration can be m(Minutest), h(Hours), d(Days))")
    private String maxWait;
    @JsonPropertyDescription(
            "Time after which failure count will be reset (duration can be m(Minutest), h(Hours), d(Days))"
    )
    private String failureResetTime;
    @JsonPropertyDescription("If login failures happens too quickly user will be locked")
    private Long quickLoginMillis;
    @JsonPropertyDescription(
            "How long to wait after a quick login failure (duration can be m(Minutest), h(Hours), d(Days))"
    )
    private String quickLoginWait;
}
