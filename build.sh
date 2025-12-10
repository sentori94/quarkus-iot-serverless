#!/bin/bash

# Script de build pour Quarkus IoT Lambda
# Migration de Spring Boot ECS vers Quarkus Lambda

set -e

echo "=========================================="
echo "Building Quarkus IoT Lambda"
echo "Migration: Spring Boot ECS → Quarkus Lambda"
echo "=========================================="

# Clean previous builds
echo "🧹 Cleaning previous builds..."
mvn clean

# Build native executable
echo "🔨 Building native executable with Quarkus..."
echo "   This may take a few minutes..."
mvn package -Pnative -Dquarkus.native.container-build=true

# Package for Lambda
echo "📦 Packaging for AWS Lambda..."
cd target

if [ -f function.zip ]; then
    rm function.zip
fi

# Créer le fichier bootstrap pour Lambda custom runtime
if [ -f *-runner ]; then
    # Le fichier bootstrap est déjà créé par Quarkus pour Lambda
    zip -j function.zip bootstrap *-runner
    FILE_SIZE=$(ls -lh function.zip | awk '{print $5}')
    echo "✅ Lambda package created: function.zip ($FILE_SIZE)"
else
    echo "❌ Error: Native executable not found"
    exit 1
fi

cd ..

echo ""
echo "=========================================="
echo "✅ Build completed successfully!"
echo "=========================================="
echo "📦 Artifact: target/function.zip"
echo "🚀 Next steps:"
echo "   1. Deploy: ./deploy.sh"
echo "   2. Or use SAM: sam deploy --guided"
echo ""
