FROM alpine:latest as cacher

WORKDIR /opt/keycloak/providers

RUN apk add --no-cache curl

# Prometheus metrics exporter for every realm and restricted access filter
RUN curl -sL https://github.com/sventorben/keycloak-restrict-client-auth/releases/download/v21.0.0/keycloak-restrict-client-auth.jar -o /opt/keycloak/providers/keycloak-restrict-client-auth.jar

FROM nexus.kacd.kz:5000/devops-tools/keycloak/keycloak:21.0.0 as builder

# Enable health and metrics support
ENV KC_HEALTH_ENABLED=true
ENV KC_METRICS_ENABLED=true

# Configure a database vendor
ENV KC_DB=postgres
ENV KC_CACHE=ispn
ENV KC_CACHE_STACK=kubernetes

WORKDIR /opt/keycloak

# Installing extensions
COPY --from=cacher /opt/keycloak/providers/keycloak-restrict-client-auth.jar /opt/keycloak/providers/keycloak-restrict-client-auth.jar
COPY build-extensions/common-extensions.jar /opt/keycloak/providers/common-extensions.jar
COPY build-extensions/external-extensions.jar /opt/keycloak/providers/external-extensions.jar
COPY build-extensions/internal-keycloak-theme.jar /opt/keycloak/providers/internal-keycloak-theme.jar
COPY build-extensions/external-keycloak-theme.jar /opt/keycloak/providers/external-keycloak-theme.jar
COPY build-extensions/afr-keycloak-theme.jar /opt/keycloak/providers/afr-keycloak-theme.jar

# building keycloak distribution and creating dir for realm import
RUN /opt/keycloak/bin/kc.sh build --spi-authenticationSessions-provider=redis && mkdir -p /opt/keycloak/.kacd/import

# Running keycloak
FROM nexus.kacd.kz:5000/devops-tools/keycloak/keycloak:21.0.0
ENV TZ="Asia/Almaty"

COPY --from=builder /opt/keycloak/ /opt/keycloak/

ENTRYPOINT ["/opt/keycloak/bin/kc.sh", "start", "--optimized"]
