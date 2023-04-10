# Single-Sign-On

Сервис Единой точки входа в систему.

Управляет пользователями, доступами и приложениями.

## Структура проекта

В директориях:
- `apps` - исходный код приложений, связанных с SSO
- `realms` - конфигурации реалмов
- `clients` - конфигурации клиентов
- `redis` - ресурсы для наката redis

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

**GET** `/org/{id}`

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

**GET** `/org/{id}/members`

**PATH**
- id - Идентификатор организации

### Response

**responseCode** `200`

**Body**
```json
[{
    "id": "0e3dcc38-d366-4d63-bf95-7ead8a50ad50",
    "name": "HEAD",
    "description": "Первый руководитель",
    "user": {
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
    },
    "configrmed": false
}]
```

---
## Подтвердить учетку сотрудника

### Request

**PATCH** `/org/{id}/members/{memberId}/confirm`

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
