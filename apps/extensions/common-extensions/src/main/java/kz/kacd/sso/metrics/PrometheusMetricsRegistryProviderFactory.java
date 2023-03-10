package kz.kacd.sso.metrics;

import com.google.auto.service.AutoService;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

@AutoService(MetricsRegistryProviderFactory.class)
public class PrometheusMetricsRegistryProviderFactory implements MetricsRegistryProviderFactory {

    public static final String PROVIDER_ID = "prometheus";

    private static final MeterRegistry registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);

    @Override
    public MetricsRegistryProvider create(KeycloakSession session) {
        return new PrometheusMetricsRegistryProvider(registry);
    }

    @Override
    public void init(Config.Scope config) {
        // Nothing to do
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        // Nothing to do
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
