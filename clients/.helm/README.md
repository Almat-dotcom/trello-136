# Helm чарт для keycloak


Данный чарт развертывает весь стек SSO-сервера.

## Конфигурация

Для каждой среды заводится файл `values.yaml` с именем среды.

Для **dev** среды файл `dev.yaml`.

### Базовый туториал для обязательного конфигурирования

Во-первых, необходимо создать в середе необходимые секреты:
- Секрет с docker.config.
  Для этого нужно сделать `docker login <registry-url>` на машине, и получить конфигурацию с авторизационными данными. Например, при помощи команды: `cat ~/.docker/config.json | base64 -w 0`. Это значение необходимо вставить в секрет:
  ```yaml
  apiVersion: v1
  kind: Secret
  metadata:
    name: kacd-docker-creds
    namespace: sso
  type: kubernetes.io/dockerconfigjson
  data:
    .dockerconfigjson: <base64-encoded-config.json>
  ```
- Секрет с TLS сертификатами (можно самоподписанные и кривые, если планируется использовать TLS на ингресс, а в keycloak обращаться по HTTP). И сертификат, и ключ нужно закодировать в base64 (`cat <file> | base64 -w 0`) и заполнить секрет:
  ```yaml
  apiVersion: v1
  kind: Secret
  metadata:
    name: keycloak-tls
    namespace: sso
  type: Opaque
  data:
    tls.crt: <base64-encoded-certificate>
    tls.key: <base64-encoded-key>
  ```
- Секрет с паролем пользователя администратора. Для этого нужно закодировать его в Base64 и добавить его в секрет:
  ```yaml
  apiVersion: v1
  kind: Secret
  metadata:
    name: sso-keycloak-admin-password
    namespace: sso
  type: Opaque
  data:
    password: <base64-encoded-admin-password>
  ```
- Секрет с паролем от базы данных. Для этого нужно пароль закодировать в Base64 и добавить секрет:
  ```yaml
  apiVersion: v1
  kind: Secret
  metadata:
    name: sso-keycloak-database-password
    namespace: sso
  type: Opaque
  data:
    password: <base64-encoded-database-password>
  ```

Далее необходимо прописать значения внутри вашего файла со значениями.

1. Глобальные настройки реестра образов. Написать базовый url реестра и названия секрета с .dockerconfig:
  ```yaml
  global:
    # Базовый URL от реестра образов
    imageRegistry: nexus.kacd.kz:5000
    # Список всех секретов с конфигом докера !ВАЖНО! перечисляются просто как список имен
    imagePullSecrets:
      - kacd-docker-creds
  ```
2. Имя образа главного контейнера keycloak в `image: nexus.kacd.kz:5000/my/repository/keycloak:latest`.
3. Количество реплик (рекомендуется не меньше 3 для обновления без простоя): `replicaCount: 3`.
4. Имя хоста (хостнейм обычно указывается такой же, как для ингресса, будьте внимательны! Keycloak очень привередлив с этим): `hostname: my-favourite-keycloak.kacd.kz`
5. Запросы и лимиты ресурсов точь-в-точь как в kubernetes. Запросы ресурсов в requests, а реальные лимиты в limits:
  ```yaml
  resources:
  limits: 
    memory: "4Gi"
    cpu: "4"
  requests:
    memory: "1Gi"
    cpu: "4"
  ```
6. Обязательно! Если запуск идет в профиле production, то необходимо сконфигурировать tls для keycloak. Нужно прописать свойство enabled, название секрета с вашими сертификатами и прописать ключ внутри секрета для сертификаты и для ключа:
  ```yaml
  tls:
    enabled: true
    existingSecret: keycloak-tls
    certificateFileKey: tls.crt
    keyFileKey: tls.key
  ```
7. Данные по авторизации администратора keycloak. Там нужно прописать логин админа и имя и ключ секрета пароля от администратора.
  ```yaml
  auth:
    adminUser: admin
    existingSecret: "sso-keycloak-admin-password"
    existingSecretPasswordKey: password
  ```
8. Также необходимо настроить подключение к базе. Самые важные настройки - это хост сервера бд, название базы данных, название схемы, имя пользователя и имя и ключ секрета от пароля к бд.
  ```yaml
  database:
    host: localhost
    name: postgres
    schema: public
    username: postgres
    existingSecret: sso-keycloak-database-password
    existingSecretPasswordKey: password
  ```
9. Если не установлена служба балансировщика нагрузки, то нужно поменять тип сервиса на ClusterIP:
  ```yaml
  service:
    type: ClusterIP
  ```
10. Настройки ingress необходимы, чтобы создать точку входу в кластер. Необходимо настроить имя хоста и TLS:
  ```yaml
  ingress:
    enabled: true
    hostname: my-favourite-keycloak.kacd.kz
    tls: true
    secrets:
      - name: keycloak-tls
        key: tls.key
        certificate: tls.crt
  ```

## Инсталяция и обновление

Инсталируется чарт при помощи helm install, а обновляется при помощи helm upgrade. Эти команды можно совместить при помощи команды: `helm upgrade --install`.

Для применения чарта можно выполнить команду:
```shell
helm upgrade --install sso . --create-namespace -n sso -f dev.yaml
```
Если все ок, то вы увидете приветственное сообщение.

Обновления происходит через RollingUpdate по одному поду, поэтому выкатывание обновлений идет без простоя самого keycloak, если количество реплик указано 3 и больше.

> ВАЖНО! Не обновляйте мажорную версию keycloak. Это приведет к модификации структуры бд и кеша и просто остановит работу сервиса. Для обновления мажорной или минорной версии необходимо полностью удалить релиз `helm uninstall sso -n sso` и заново его установить.
