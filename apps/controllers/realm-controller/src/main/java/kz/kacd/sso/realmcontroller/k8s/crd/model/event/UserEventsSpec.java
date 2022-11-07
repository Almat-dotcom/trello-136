package kz.kacd.sso.realmcontroller.k8s.crd.model.event;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;

import java.util.List;

@Data
public class UserEventsSpec {

    @JsonPropertyDescription("If true user events will be enabled")
    private Boolean saveEvents;
    @JsonPropertyDescription("Duration of user events persist time (duration can be m(Minutest), h(Hours), d(Days))")
    private String expiration;
    @JsonPropertyDescription(
            "List of event types which will be saved. If empty and events active, it creates standard list of types."
    )
    private List<String> savedTypes;
}
