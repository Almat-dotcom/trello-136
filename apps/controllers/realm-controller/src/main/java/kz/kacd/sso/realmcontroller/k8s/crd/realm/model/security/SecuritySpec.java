package kz.kacd.sso.realmcontroller.k8s.crd.realm.model.security;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;

@Data
public class SecuritySpec {

    @JsonPropertyDescription("Brute force detection config")
    private BruteForceSpec bruteForce;
}
