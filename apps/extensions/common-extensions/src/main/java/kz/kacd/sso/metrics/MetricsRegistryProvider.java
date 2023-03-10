package kz.kacd.sso.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import org.keycloak.provider.Provider;

public interface MetricsRegistryProvider extends Provider {

    MeterRegistry provide();
}
