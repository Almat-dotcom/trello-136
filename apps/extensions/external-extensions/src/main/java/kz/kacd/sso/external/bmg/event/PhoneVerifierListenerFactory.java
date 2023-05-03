package kz.kacd.sso.external.bmg.event;

import com.google.auto.service.AutoService;
import org.keycloak.Config;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.RealmModel;
import org.keycloak.models.utils.KeycloakModelUtils;
import org.keycloak.models.utils.PostMigrationEvent;

import java.util.Set;
import java.util.stream.Collectors;

@AutoService(EventListenerProviderFactory.class)
public class PhoneVerifierListenerFactory implements EventListenerProviderFactory {

    public static final String PROVIDER_ID = "phone-verifier-listener";

    @Override
    public EventListenerProvider create(KeycloakSession session) {
        return new PhoneVerifierListener(session);
    }

    @Override
    public void init(Config.Scope config) {
        // Nothing to do
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        factory.register(event -> {
            if (event instanceof PostMigrationEvent) {
                KeycloakModelUtils.runJobInTransaction(
                        ((PostMigrationEvent) event).getFactory(),
                        session -> session.realms().getRealmsStream().forEach(this::checkListenerSettings)
                );
            } else if (event instanceof RealmModel.RealmPostCreateEvent) {
                RealmModel.RealmPostCreateEvent createEvent = (RealmModel.RealmPostCreateEvent) event;
                checkListenerSettings(createEvent.getCreatedRealm());
            }
        });
    }

    private void checkListenerSettings(RealmModel realm) {
        if (
                realm.getEventsListenersStream().anyMatch(PROVIDER_ID::equals)
        ) {
            return;
        }

        Set<String> listeners = realm.getEventsListenersStream().collect(Collectors.toSet());
        listeners.add(PROVIDER_ID);
        realm.setEventsListeners(listeners);
    }

    @Override
    public void close() {
        // Nothing to close
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }
}
