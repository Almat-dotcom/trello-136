package kz.kacd.sso.realmcontroller.k8s.crd.model.event;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;

import java.util.List;

@Data
public class EventsSpec {

    @JsonPropertyDescription("List of event listeners")
    private List<String> eventListeners;
    @JsonPropertyDescription("Config of user events")
    private UserEventsSpec userEvents;
    @JsonPropertyDescription("Config of admin events")
    private AdminEventsSpec adminEvents;
}
