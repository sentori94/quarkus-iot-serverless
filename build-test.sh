#!/bin/bash

# Script de build simple pour test
echo "=========================================="
echo "Building Quarkus IoT Lambda (JVM mode)"
echo "=========================================="

# Clean et build en mode JVM (plus rapide pour tester)
mvn clean package -DskipTests

echo ""
echo "=========================================="
echo "Build completed!"
echo "=========================================="
echo ""
echo "📦 Package créé:"
ls -lh target/*.jar 2>/dev/null || echo "Aucun JAR trouvé"

echo ""
echo "📁 Contenu du répertoire target/:"
ls -lh target/ 2>/dev/null || echo "Répertoire target vide"

echo ""
echo "🔍 Pour voir le contenu du JAR:"
echo "   unzip -l target/quarkus-iot-lambda-1.0.0-SNAPSHOT-runner.jar | head -50"
