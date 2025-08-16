#!/bin/bash

echo "🔧 ТЕСТИРОВАНИЕ ВОССТАНОВЛЕННОЙ ЛОГИКИ CONFIGURATIONRESOURCE"
echo "============================================================="

# Настройки
KEYCLOAK_URL="http://localhost:8084"
REALM="master"
CLIENT_ID="api-client"
CLIENT_SECRET="your-client-secret"

echo "📋 Настройки:"
echo "   Keycloak URL: $KEYCLOAK_URL"
echo "   Realm: $REALM"
echo "   Client ID: $CLIENT_ID"
echo ""

echo "🔑 Шаг 1: Получение токена доступа"
echo "----------------------------------"

# Получаем токен доступа
TOKEN_RESPONSE=$(curl -s -X POST \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=client_credentials" \
  -d "client_id=$CLIENT_ID" \
  -d "client_secret=$CLIENT_SECRET" \
  "$KEYCLOAK_URL/auth/realms/$REALM/protocol/openid-connect/token")

echo "Ответ от Keycloak:"
echo "$TOKEN_RESPONSE" | jq '.' 2>/dev/null || echo "$TOKEN_RESPONSE"
echo ""

# Извлекаем токен доступа
ACCESS_TOKEN=$(echo "$TOKEN_RESPONSE" | jq -r '.access_token' 2>/dev/null)

if [ "$ACCESS_TOKEN" = "null" ] || [ -z "$ACCESS_TOKEN" ]; then
    echo "❌ Ошибка получения токена!"
    echo "Проверьте настройки клиента в Keycloak Admin Console"
    exit 1
fi

echo "✅ Токен получен успешно!"
echo "Токен (первые 50 символов): ${ACCESS_TOKEN:0:50}..."
echo ""

echo "🌐 Шаг 2: Тестирование восстановленной логики ConfigurationResource"
echo "----------------------------------------------------------------"

# Тестируем различные endpoints с восстановленной логикой
ENDPOINTS=(
    "configuration/status"
    "configuration/realm"
    "configuration/federation"
    "configuration/clients"
)

for endpoint in "${ENDPOINTS[@]}"; do
    echo "🔍 Тестируем: $endpoint"
    
    RESPONSE=$(curl -s -w "\nHTTP_STATUS: %{http_code}" \
        -H "Authorization: Bearer $ACCESS_TOKEN" \
        -H "Content-Type: application/json" \
        "$KEYCLOAK_URL/auth/realms/$REALM/$endpoint")
    
    HTTP_STATUS=$(echo "$RESPONSE" | grep "HTTP_STATUS:" | cut -d' ' -f2)
    BODY=$(echo "$RESPONSE" | sed '/HTTP_STATUS:/d')
    
    if [ "$HTTP_STATUS" = "200" ]; then
        echo "✅ $endpoint - OK (200)"
        echo "Ответ: $BODY"
    elif [ "$HTTP_STATUS" = "401" ]; then
        echo "❌ $endpoint - Unauthorized (401)"
        echo "Проверьте роль 'realm-config' у клиента"
    elif [ "$HTTP_STATUS" = "403" ]; then
        echo "❌ $endpoint - Forbidden (403)"
        echo "Недостаточно прав доступа"
    elif [ "$HTTP_STATUS" = "404" ]; then
        echo "⚠️  $endpoint - Not Found (404)"
        echo "Endpoint не найден"
    elif [ "$HTTP_STATUS" = "503" ]; then
        echo "⚠️  $endpoint - Service Unavailable (503)"
        echo "Kubernetes client недоступен"
    else
        echo "❓ $endpoint - HTTP $HTTP_STATUS"
        echo "Ответ: $BODY"
    fi
    echo ""
done

echo "🔧 Шаг 3: Тестирование обновления конфигураций"
echo "---------------------------------------------"

# Тестируем обновление realm конфигурации
echo "🔍 Тестируем обновление realm конфигурации..."

UPDATE_PAYLOAD='{
  "type": "REALM_CONFIG",
  "name": "master",
  "state": "APPLIED",
  "message": "Realm configuration updated successfully",
  "generation": "1",
  "lastApplication": "2025-08-16T10:00:00Z"
}'

RESPONSE=$(curl -s -w "\nHTTP_STATUS: %{http_code}" \
    -X PUT \
    -H "Authorization: Bearer $ACCESS_TOKEN" \
    -H "Content-Type: application/json" \
    -d "$UPDATE_PAYLOAD" \
    "$KEYCLOAK_URL/auth/realms/$REALM/configuration/realm")

HTTP_STATUS=$(echo "$RESPONSE" | grep "HTTP_STATUS:" | cut -d' ' -f2)
BODY=$(echo "$RESPONSE" | sed '/HTTP_STATUS:/d')

if [ "$HTTP_STATUS" = "200" ]; then
    echo "✅ Обновление realm - OK (200)"
    echo "Ответ: $BODY"
elif [ "$HTTP_STATUS" = "503" ]; then
    echo "⚠️  Обновление realm - Service Unavailable (503)"
    echo "Kubernetes client недоступен или ресурсы не найдены"
else
    echo "❓ Обновление realm - HTTP $HTTP_STATUS"
    echo "Ответ: $BODY"
fi
echo ""

echo "📋 Шаг 4: Анализ результатов"
echo "---------------------------"

echo "🔍 Возможные результаты:"
echo ""
echo "✅ УСПЕХ:"
echo "   - HTTP 200: Логика работает корректно"
echo "   - Получены данные из Kubernetes (если ресурсы существуют)"
echo "   - Обновления статусов работают"
echo ""
echo "⚠️  ОЖИДАЕМО:"
echo "   - HTTP 503: Kubernetes client недоступен (если нет k8s-bundle.jar)"
echo "   - HTTP 404: Ресурсы не найдены в Kubernetes (если не созданы)"
echo "   - HTTP 401: Проблемы с аутентификацией"
echo ""
echo "❌ ПРОБЛЕМЫ:"
echo "   - HTTP 405: Конфликты с Fabric8 (должны быть решены)"
echo "   - HTTP 500: Ошибки в коде"
echo ""
echo "📞 Следующие шаги:"
echo "   1. Если 503: Проверьте наличие k8s-bundle.jar"
echo "   2. Если 404: Создайте ресурсы в Kubernetes"
echo "   3. Если 401: Настройте роли клиента"
echo "   4. Если 200: Логика работает корректно!"

