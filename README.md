# Single-Sign-On (Monorepo)

Сервис Единой точки входа в систему.

Управляет пользователями, доступами и приложениями.

## Структура проекта

В директориях:
- `apps` - исходный код приложений, связанных с SSO
- `realms` - конфигурации реалмов
- `clients` - конфигурации клиентов

# Процесс сборки

В проекте 3 пайплайна на 3 подпроекта: apps, realms, clients.

Подпроект apps сам по себе состоит из множество расширений (библиотек, которые собираются в .jar-файлы). В стадии prebuild все расширения собираются в jar-файлы и сохраняются в nexus raw repository. После скачиваются данные jar-файлы в локальный кеш раннера и собирается docker-образ с keycloak.

# Разработка

Для редактирования стандартных конфигураций, обратитесь в под проекты `realms` и `clients`. Это подпроекты в виде helm-чартов. Для тестирования, их необходимо развернуть в kubernetes.

В apps несколько разных проектов, которые по разному собираются и работают. Обратитесь к readme конкретного проекта. Чаще всего требуется полностью пересобрать все расширения, чтобы нормально протестировать код, из-за этого приходится сразу пушить в dev-ветку, ждать развертывания, и потом тестировать. Определенные изменения можно протестировать локально. Для того, как это сделать, обратитесь к readme определенного проекта.

# API для взаимодействия с реалмами

**Base URL**
`https://<realm-host>/realms/<realmName>`

---
## Получить список профилей

### Request

**GET** `/profile`

**Query** 
- page - номер страницы, начиная с 1

### Response

**responseCode** `200`
**Body**:
```json
{
    "page": 10,
    "limit": 10,
    "totalPages": 10,
    "totalElements": 97,
    "content": [
        {
            "id": "0e475c35-ed18-4951-b583-72ed72372dc8",
            "username": "test",
            "firstName": "test",
            "enabled": true,
            "roles": {
                "realm": [
                    "default-roles-external"
                ]
            },
            "groups": ["test"]
        }
    ]
}
```

---
## Получить профиль

### Request

**GET** `/profile/{id}`

**Path** 
- id - Идентификаор пользователя

### Response

**responseCode** `200`
**Body**:
```json
{
    "id": "0e475c35-ed18-4951-b583-72ed72372dc8",
    "username": "test",
    "firstName": "test",
    "enabled": true,
    "roles": {
        "realm": [
            "default-roles-external"
        ]
    },
    "groups": ["test"]
}
```

---
## Обновить авторизационные данные

### Request

**PATCH** `/profile/{id}`

**PATH**
- id - Идентификатор пользователя

**Body**
```json
{
    "newEmail": "test@example.com",
    "newPhoneNumber": "+77777777777"
}
```

### Response

**responseCode** `202`

---
## Добавить роль к пользователю

### Request

**POST** `/profile/{id}/role/{clientId}/{roleName}`

**PATH**
- id - Идентификатор пользователя
- clientId - Идентификатор клиента
- roleName - Имя роли

### Response

**responseCode** `202`

---
## Удалить роль у пользователя

### Request

**DELETE** `/profile/{id}/role/{clientId}/{roleName}`

**PATH**
- id - Идентификатор пользователя
- clientId - Идентификатор клиента
- roleName - Имя роли

### Response

**responseCode** `202`

---
## Получить организацию по id

### Request

**GET** `/orgs/{id}`

**PATH**
- id - Идентификатор ЮЛ

### Response

**responseCode** `200`

**Body**
```json
{
    "id": "0e3dcc38-d366-4d63-bf95-7ead8a50ad50",
    "bin": "123456789012",
    "name": "test",
    "displayName": "test",
    "enabled": true,
    "createdAt": "2023-01-01T00:00:00.000",
    "updatedAt": "2023-01-01T00:00:00.000"
}
```

---
## Получить список сотрудников организации

### Request

**GET** `/orgs/{id}/members`

**PATH**
- id - Идентификатор организации

**QUERY**
- from - Номер елемента, с которого начинать страницу результата
- limit - максимальное кол-во элементов на странице
- userId - идентификатор пользователя

### Response

**responseCode** `200`

**Body**
```json
{
    "totalElements": 1,
    "from": 0,
    "limit": 100,
    "content": [
        {
            "id": "0c987e03-d9d7-405b-af8e-efdd60cef48b",
            "name": "HEAD",
            "description": "Первый руководитель",
            "user": {
                "id": "df20f94e-2f92-4590-a910-72015afd8511",
                "username": "admin",
                "enabled": true,
                "roles": {
                    "internal-realm": [
                        "query-groups",
                        "manage-identity-providers",
                        "view-events",
                        "query-users",
                        "view-authorization",
                        "manage-events",
                        "view-clients",
                        "manage-clients",
                        "query-realms",
                        "view-users",
                        "manage-authorization",
                        "manage-realm",
                        "query-clients",
                        "manage-users",
                        "view-identity-providers",
                        "create-client",
                        "view-realm"
                    ],
                    "afr-realm": [
                        "query-clients",
                        "query-groups",
                        "view-authorization",
                        "manage-events",
                        "view-events",
                        "manage-identity-providers",
                        "query-realms",
                        "view-clients",
                        "create-client",
                        "view-identity-providers",
                        "view-realm",
                        "query-users",
                        "manage-realm",
                        "manage-clients",
                        "manage-users",
                        "manage-authorization",
                        "view-users"
                    ],
                    "external-realm": [
                        "create-client",
                        "view-users",
                        "query-groups",
                        "manage-realm",
                        "view-events",
                        "manage-users",
                        "manage-identity-providers",
                        "view-identity-providers",
                        "manage-authorization",
                        "manage-events",
                        "view-authorization",
                        "view-clients",
                        "query-realms",
                        "manage-clients",
                        "query-clients",
                        "query-users",
                        "view-realm"
                    ],
                    "realm": [
                        "default-roles-master",
                        "admin"
                    ]
                },
                "groups": []
            },
            "confirmed": true
        }
    ]
}
```

---
## Подтвердить учетку сотрудника

### Request

**PATCH** `/orgs/{id}/members/{memberId}/confirm`

**PATH**
- id - Идентификатор организации
- memberId - Идентификатор позиции

### Response

**responseCode** `202`

---
## Получить список клиентов

### Request

**GET** `/realm-client-roles`

### Response

**responseCode** `200`

**Body**

```json
[{
    "clientId": "test",
    "name": "Test",
    "description": "Test"
}]
```

---
## Получить список ролей по клиенту

### Request

**GET** `/realm-client-roles/{clientId}/role`

**PATH**
- clientId - Идентификатор клиента

### Response

**responseCode** `200`

**Body**
```json
[{
    "name": "depositor",
    "description": "Депонент"
}]
```

---
## Задать признак прохождения УДО ФЛ

### Request

**POST** `/ebr/physical`

**Body**:
```json
{
    "id": "123kjakd-jkjakskda-213123-asdhjkasd",
    "sign": true
}
```

### Response

**responseCode** `202`

---
## Задать признак прохождения УДО ЮЛ

### Request

**POST** `/ebr/legal`

**Body**:
```json
{
    "id": "123kjakd-jkjakskda-213123-asdhjkasd",
    "sign": true
}
```

### Response

**responseCode** `202`
