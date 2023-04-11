package kz.kacd.sso.external.kafka.impl;

import com.google.auto.service.AutoService;
import kz.kacd.sso.external.kafka.KafkaProvider;
import kz.kacd.sso.external.kafka.KafkaProviderFactory;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@AutoService(KafkaProviderFactory.class)
public class DefaultKafkaProviderFactory implements KafkaProviderFactory {

    public static final String PROVIDER_ID = "default-kafka-provider";

    private final Map<String, KafkaProvider> providers = new ConcurrentHashMap<>();

    @Override
    public KafkaProvider create(KeycloakSession session) {
        KafkaProvider result = new DefaultKafkaProvider();
        providers.put(UUID.randomUUID().toString(), result);
        return result;
    }

    @Override
    public void init(Config.Scope config) {
        // Nothing to init
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        // Nothing to init
    }

    @Override
    public void close() {
        providers.forEach((k, v) -> v.close());
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }
}
