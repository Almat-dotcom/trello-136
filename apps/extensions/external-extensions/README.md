# External Extensions

Библиотека расширений keycloak для внешнего реалма.

## Разработка

Для разработки необходимо:
- jdk 11
- maven 3.6.x и выше
- docker (или Docker Desktop)
- Ваша любимая IDE

## Тестирование с Keycloak

1. Необходимо собрать jar-архив: `mvn clean package`
2. Поднять keycloak: `docker compose -f keycloak.yaml up`

На порту 8080 будет поднят Keycloak. На порту 5432 будет поднят PostgreSQL.

Для тестирования в Postman можете импортировать коллекцию
`External_Organization_Tests.postman_collection.json`
