package kz.kacd.sso.realmcontroller.k8s.crd.client.model.role;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;

@Data
public class ClientRole {

    @JsonProperty("Name of the role. This name will be propagated into tokens.")
    private String name;
    @JsonPropertyDescription("Description of the role. It can be multiline string.")
    private String description;
}
