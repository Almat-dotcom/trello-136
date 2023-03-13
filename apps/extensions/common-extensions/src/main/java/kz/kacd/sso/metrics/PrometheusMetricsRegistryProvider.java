package kz.kacd.sso.metrics;

import io.micrometer.core.instrument.MeterRegistry;

public class PrometheusMetricsRegistryProvider implements MetricsRegistryProvider {

    private final MeterRegistry registry;

    public PrometheusMetricsRegistryProvider(MeterRegistry registry) {
        this.registry = registry;
    }

    @Override
    public MeterRegistry provide() {
        return registry;
    }

    @Override
    public void close() {
        // Nothing to do
    }
}
