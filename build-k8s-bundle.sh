#!/bin/bash

# Скрипт для сборки и развертывания k8s-bundle

set -e

echo "Building k8s-bundle..."
cd apps/extensions/k8s-bundle
mvn clean package

echo "Building common-extensions..."
cd ../common-extensions
mvn clean package

echo "Copying k8s-bundle.jar to common-extensions target..."
cp ../k8s-bundle/target/k8s-bundle.jar target/

echo "Build completed successfully!"
echo ""
echo "Files created:"
echo "- apps/extensions/k8s-bundle/target/k8s-bundle.jar"
echo "- apps/extensions/common-extensions/target/common-extensions.jar"
echo ""
echo "To deploy:"
echo "1. Copy k8s-bundle.jar to Keycloak providers directory"
echo "2. Copy common-extensions.jar to Keycloak providers directory"
echo "3. Restart Keycloak"

