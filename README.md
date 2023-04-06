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
