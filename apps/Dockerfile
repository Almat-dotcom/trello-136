FROM quay.io/keycloak/keycloak:20.0.2 as builder

# Enable health and metrics support
ENV KC_HEALTH_ENABLED=true
ENV KC_METRICS_ENABLED=true

# Configure a database vendor
ENV KC_DB=postgres

WORKDIR /opt/keycloak

# Prometheus metrics exporter for every realm and restricted access filter
RUN curl -sL https://github.com/aerogear/keycloak-metrics-spi/releases/download/2.5.3/keycloak-metrics-spi-2.5.3.jar -o /opt/keycloak/providers/keycloak-metrics-spi.jar \
    && curl -sL https://github.com/sventorben/keycloak-restrict-client-auth/releases/download/v20.0.0/keycloak-restrict-client-auth.jar -o /opt/keycloak/providers/keycloak-restrict-client-auth.jar

# Installing extensions
COPY build-extensions/common-extensions.jar /opt/keycloak/providers/common-extensions.jar
COPY build-extensions/external-extensions.jar /opt/keycloak/providers/external-extensions.jar
COPY build-extensions/internal-keycloak-theme.jar /opt/keycloak/providers/internal-keycloak-theme.jar
COPY build-extensions/external-keycloak-theme.jar /opt/keycloak/providers/external-keycloak-theme.jar

# building keycloak distribution and creating dir for realm import
RUN /opt/keycloak/bin/kc.sh build && mkdir -p /opt/keycloak/.kacd/import

# Running keycloak
FROM quay.io/keycloak/keycloak:20.0.2
COPY --from=builder /opt/keycloak/ /opt/keycloak/

ENTRYPOINT ["/opt/keycloak/bin/kc.sh", "start", "--optimized"]
