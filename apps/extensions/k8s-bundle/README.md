# K8s Bundle - Изолированный Fabric8 Kubernetes Client

Этот модуль содержит изолированную версию Fabric8 Kubernetes Client для избежания конфликтов с Keycloak 23.

## Проблема

При обновлении Keycloak до версии 23 возникают конфликты с Fabric8 Kubernetes Client, что приводит к ошибкам 405 при вызове API.

## Решение

Создан отдельный JAR `k8s-bundle.jar` который содержит:
- Fabric8 Kubernetes Client с релокацией пакетов
- Сгенерированные модели Kubernetes ресурсов
- Фасад для работы с изолированным клиентом

## Сборка

```bash
cd apps/extensions/k8s-bundle
mvn clean package
```

Это создаст `target/k8s-bundle.jar` - fat JAR с изолированными зависимостями.

## Использование

1. Соберите k8s-bundle:
   ```bash
   cd apps/extensions/k8s-bundle
   mvn clean package
   ```

2. Соберите common-extensions:
   ```bash
   cd apps/extensions/common-extensions
   mvn clean package
   ```

3. Скопируйте k8s-bundle.jar в нужное место:
   ```bash
   cp ../k8s-bundle/target/k8s-bundle.jar /path/to/keycloak/providers/
   ```

## Архитектура

- `K8sBundleFacade` - фасад для работы с изолированным клиентом
- `IsolatedK8sClientProvider` - провайдер который загружает k8s-bundle через URLClassLoader
- Изолированные репозитории используют reflection для работы с фасадом

## Преимущества

- Полная изоляция Fabric8 от основного classpath
- Нет конфликтов с Keycloak 23
- Возможность использовать разные версии Fabric8
- Простота развертывания

## Структура пакетов

После релокации пакеты имеют префикс `kz.kacd.sso.k8s.bundle`:
- `kz.kacd.sso.k8s.bundle.fabric8.*` - Fabric8 классы
- `kz.kacd.sso.k8s.bundle.okhttp3.*` - OkHttp классы
- `kz.kacd.sso.k8s.bundle.jackson.*` - Jackson классы

