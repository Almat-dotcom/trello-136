package kz.kacd.sso.realmcontroller.k8s.crd.model.event;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;

@Data
public class AdminEventsSpec {

    @JsonPropertyDescription("If true admin events will be stored")
    private Boolean saveEvents;
    @JsonPropertyDescription("If true admin events will be stored with it's representations")
    private Boolean includeRepresentation;
    @JsonPropertyDescription("expiration of the admin events (duration can be m(Minutest), h(Hours), d(Days))")
    private String expiration;
}
