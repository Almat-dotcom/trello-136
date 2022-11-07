#
# Main image with execution user created
#
FROM amazoncorretto:17-alpine3.16-jdk as run

RUN adduser -H -u 1000 -D realm

#
# Image to build app
#
FROM amazoncorretto:17-alpine3.16-jdk as build

WORKDIR /build

# Copiyng base files to save dependencies
COPY gradle gradle
COPY gradlew gradlew
COPY settings.gradle settings.gradle
COPY build.gradle build.gradle
COPY .build/Main.java src/main/java/Main.java
COPY .build/Main.java src/test/java/Main.java
# Saving dependencies and remove extra classes
RUN ./gradlew clean build --no-daemon -i --stacktrace || true \
        && rm -rf src

# Building app
COPY . .
RUN ./gradlew clean build --no-daemon -i --stacktrace

#
# Packaging final image
#
FROM run

# Copiyng artifact of the build
COPY --from=build --chown=1000:1000 /build/build/libs/realm-controller-0.0.1.jar /home/realm/app.jar

# Exposing standard execution port
EXPOSE 8080
# SU to execution user
USER 1000

# Running app
ENTRYPOINT ["java", "-jar", "/home/realm/app.jar"]
