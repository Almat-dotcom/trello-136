#!/bin/bash

echo "🚀 ИНТЕГРАЦИЯ РЕШЕНИЯ В KEYCLOAK"
echo "================================"

# Проверяем наличие файлов
K8S_BUNDLE="apps/extensions/k8s-bundle/target/k8s-bundle.jar"
COMMON_EXTENSIONS="apps/extensions/common-extensions/target/common-extensions.jar"
KEYCLOAK_YAML="apps/extensions/common-extensions/keycloak.yaml"

if [ ! -f "$K8S_BUNDLE" ]; then
    echo "❌ k8s-bundle.jar не найден: $K8S_BUNDLE"
    exit 1
fi

if [ ! -f "$COMMON_EXTENSIONS" ]; then
    echo "❌ common-extensions.jar не найден: $COMMON_EXTENSIONS"
    exit 1
fi

if [ ! -f "$KEYCLOAK_YAML" ]; then
    echo "❌ keycloak.yaml не найден: $KEYCLOAK_YAML"
    exit 1
fi

echo "✅ k8s-bundle.jar найден: $(ls -lh $K8S_BUNDLE | awk '{print $5}')"
echo "✅ common-extensions.jar найден: $(ls -lh $COMMON_EXTENSIONS | awk '{print $5}')"
echo "✅ keycloak.yaml найден: $KEYCLOAK_YAML"

# Останавливаем Keycloak если запущен
echo ""
echo "🛑 Остановка Keycloak..."
docker compose -f "$KEYCLOAK_YAML" down

# Создаем директории если не существуют
echo ""
echo "📁 Создание директорий..."
mkdir -p keycloak/standalone/lib
mkdir -p keycloak/standalone/deployments
mkdir -p keycloak/standalone/configuration

# Копируем файлы
echo ""
echo "📦 Копирование файлов..."

# Копируем k8s-bundle в lib
cp "$K8S_BUNDLE" keycloak/standalone/lib/
echo "✅ k8s-bundle.jar скопирован в keycloak/standalone/lib/"

# Копируем common-extensions в deployments
cp "$COMMON_EXTENSIONS" keycloak/standalone/deployments/
echo "✅ common-extensions.jar скопирован в keycloak/standalone/deployments/"

# Проверяем права доступа
chmod 644 keycloak/standalone/lib/k8s-bundle.jar
chmod 644 keycloak/standalone/deployments/common-extensions.jar

echo ""
echo "🔧 Настройка провайдеров..."

# Создаем application.properties с настройками провайдеров
cat > keycloak/standalone/configuration/application.properties << EOF
# K8s Client Provider Configuration
k8s.client.provider=isolated-k8s-client-provider
k8s.client.spec.provider=isolated-k8s-client-spec-provider

# Logging for debugging
logging.level.kz.kacd.sso.k8s=DEBUG
EOF

echo "✅ application.properties создан с настройками провайдеров"

echo ""
echo "🚀 Запуск Keycloak..."
docker compose -f "$KEYCLOAK_YAML" up -d

echo ""
echo "⏳ Ожидание запуска Keycloak..."
sleep 15

echo ""
echo "📋 Проверка статуса контейнеров..."
docker compose -f "$KEYCLOAK_YAML" ps

echo ""
echo "📖 Логи Keycloak (последние 30 строк):"
docker compose -f "$KEYCLOAK_YAML" logs --tail=30 keycloak

echo ""
echo "🎉 ИНТЕГРАЦИЯ ЗАВЕРШЕНА!"
echo "======================="
echo ""
echo "📋 Что было сделано:"
echo "✅ k8s-bundle.jar скопирован в keycloak/standalone/lib/"
echo "✅ common-extensions.jar скопирован в keycloak/standalone/deployments/"
echo "✅ application.properties настроен с провайдерами"
echo "✅ Keycloak перезапущен"
echo ""
echo "🔍 Для мониторинга логов:"
echo "   docker compose -f $KEYCLOAK_YAML logs -f keycloak"
echo ""
echo "🌐 Keycloak доступен по адресу:"
echo "   http://localhost:8080"
echo ""
echo "📞 Для тестирования API:"
echo "   curl -X GET http://localhost:8080/auth/realms/master/protocol/openid-connect/userinfo"
echo ""
echo "🔍 Для проверки отсутствия 405 ошибок:"
echo "   docker compose -f $KEYCLOAK_YAML logs keycloak | grep -E '(ERROR|WARN|405|Fabric8)'"
