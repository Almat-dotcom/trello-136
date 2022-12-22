FROM quay.io/quarkus/quarkus-micro-image:1.0 AS run
WORKDIR /work/

RUN chown 1001 /work \
    && chmod "g+rwX" /work \
    && chown 1001:root /work

FROM quay.io/quarkus/ubi-quarkus-mandrel-builder-image:22.3-java17 AS build
WORKDIR /build
USER root
RUN microdnf install maven -y

COPY ./pom.xml ${WORKDIR}
RUN mvn quarkus:go-offline --fail-never

COPY . .
RUN mvn install -Dnative

FROM run
WORKDIR /work/

COPY --from=build --chown=1001:root /build/target/*-runner /work/application

EXPOSE 8080
USER 1001

CMD ["./application", "-Dquarkus.http.host=0.0.0.0"]
