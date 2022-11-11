package kz.kacd.sso.realmcontroller.k8s.crd.realm.model.login;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;

@Data
public class LoginUserInfoSpec {

    @JsonPropertyDescription("Are users allowed to edit usernames")
    private Boolean editUsername;
}
