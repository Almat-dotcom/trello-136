# Проект темы для внешних пользователей SSO

# Разработка

Для разработки необходимо установить:
  - node js версии 18.12.1
  - Docker (или Docker desktop)

## До начала разработки

Да начала разработки необходимо выполнить первую сборку проекта. Это может занять много времени, но необходимо.

Необходимо произвести первичную сборку проекта. Для этого необходим войти в реестр образов ЦД под своей учетной записью:
```shell
docker login nexus.kacd.kz:5000
```

И далее выполнить для **Linux**:
```shell
docker container run -v $PWD:/app:rw --privileged --rm nexus.kacd.kz:5000/devops-utils/keycloakify-build-image:latest
```

Для **Widnows**:
```batch
docker container run -v ${pwd}:/app:rw --privileged --rm nexus.kacd.kz:5000/devops-utils/keycloakify-build-image:latest
```

Это может занять 20-30 минут.

Выполните установку зависимостей:
```shell
yarn install
```

> Если у вас не установлен yarn, то просто выполните:
> ```shell
> npm install -g yarn
> ```

Теперь проект полностью готов к ведению разработки.

## Разработка

Для запуска реактовского сервера просто выполните:
```shell
yarn start
```

Нужно будет подождать пока сервер не загрузит страницы.

Любое изменение в коде будет пораждать Hot-Reaload.

# Тестирование с реальным keycloak

Для тестирования необходимо снова собрать проект. Для ускорении сборки можно воспользоваться следующими командами:
для **Linux**:
```shell
docker container run -v $PWD:/app:rw --privileged --rm --entrypoint="/bin/sh" nexus.kacd.kz:5000/devops-utils/keycloakify-build-image:latest -c "cd /app && yarn keycloak"
```

Для **Widnows**:
```batch
docker container run -v ${pwd}:/app:rw --privileged --rm --entrypoint="/bin/sh" nexus.kacd.kz:5000/devops-utils/keycloakify-build-image:latest -c "cd /app && yarn keycloak"
```

И затем выполнить:
```shell
docker compose -f keycloak.yaml up
```

Как только в консоле появится надпись "DO NOT use this configuration in production", вы можете перейти по ссылке http://localhost:8080/realms/Test/account и на странице нажать "Sign in". Тестовый пользователь - test:test
