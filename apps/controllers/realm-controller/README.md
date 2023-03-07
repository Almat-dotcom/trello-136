# realm-controller Project

Realm controller - контроллер (оператор kubernetes), который слушает изменеия
в ресурсах конфигурации keycloak на API сервере Kubernetes и уведомляет Keycloak
обо всех изменениях.

## Разработка

Для разработки необходимо:
- Для всех сред разработки:
  - jdk 17
  - maven 3.6.x и выше
  - docker (Docker Desktop)
  - Ваша любимая IDE с плагином Quarkus
- Для VS Code:
  - плагин quarkus

Перед началом разработки рекомендуется выполнить `mvn clean compile`, чтобы с 
Custom Resource Definition сгенерировался код.

Для запуска разработческого сервера нужно выполнить:
```shell
mvn quarkus:dev
```

[Консоль разработчика](http://localhost:8080/q/dev)

## Сборка

### Обычная сборка

Для сборки проекта нужно выполнить команду:
```shell
mvn clean package
```

После сборки в `target` будет .jar-файл с приложением. 
В папке `target/quarkus-app` будет конечная сборка приложения, а именно:
- В `app` сам jar-файл с приложением
- в `lib` все зависимости проекта для classpath
- `quarkus-run.jar` стартер для приложения.

Для сборки uber-jar необходимо выполнить команду:
```shell
mvn clean package -Dquarkus.package.type=uber-jar
```

Uber-jar будет в `target`.

### Нативная сборка

Для нативной сборки необходимо установить GraalVM или Docker.

При установленной GraalVM необходимо выполнить команду:
```shell
mvn install -Pnative
```

Для сборки в Docker или Podman нужно выполнить команду:
```shell
mvn install -Pnative -DskipTests -Dquarkus.native.container-build=true
```

### Сборка образа контейнера

Для сборки необходимо выполнить:
```shell
docker build -t realm-controller:latest .
```

## Related Guides

- OpenID Connect Client ([guide](https://quarkus.io/guides/security-openid-connect-client)): Get and refresh access tokens from OpenID Connect providers
- Micrometer Registry Prometheus ([guide](https://quarkus.io/guides/micrometer)): Enable Prometheus support for Micrometer
- REST Client Classic ([guide](https://quarkus.io/guides/rest-client)): Call REST services
- OpenID Connect Client Filter ([guide](https://quarkus.io/guides/security-openid-connect-client)): Use JAX-RS Client filter to get and refresh access tokens with OpenId Connect Client and send them as HTTP Authorization Bearer tokens
- Kubernetes Client ([guide](https://quarkus.io/guides/kubernetes-client)): Interact with Kubernetes and develop Kubernetes Operators
- OpenID Connect Token Propagation ([guide](https://quarkus.io/guides/security-openid-connect-client)): Use JAX-RS Client filter to propagate the incoming Bearer access token or token acquired from Authorization Code Flow as HTTP Authorization Bearer token
- Logging JSON ([guide](https://quarkus.io/guides/logging#json-logging)): Add JSON formatter for console logging
- SmallRye Health ([guide](https://quarkus.io/guides/microprofile-health)): Monitor service health
- Micrometer metrics ([guide](https://quarkus.io/guides/micrometer)): Instrument the runtime and your application with dimensional metrics using Micrometer.
