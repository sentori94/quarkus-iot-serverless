# Structure du package Quarkus IoT Lambda

## 📦 Ce qui sera packagé

### Mode JVM (pour dev/test rapide)
```
target/quarkus-iot-lambda-1.0.0-SNAPSHOT-runner.jar
│
├── com/sentori/iot/lambda/
│   ├── model/
│   │   ├── SensorData.class
│   │   ├── RunEntity.class
│   │   ├── RunStartRequest.class
│   │   ├── RunStartResponse.class
│   │   └── CanStartRunResponse.class
│   ├── repository/
│   │   ├── SensorDataRepository.class
│   │   └── RunRepository.class
│   ├── service/
│   │   ├── SensorService.class
│   │   └── RunService.class
│   ├── resource/
│   │   ├── SensorResource.class
│   │   ├── RunResource.class
│   │   └── HealthResource.class
│   └── util/
│       └── GrafanaUrlBuilder.class
├── application.properties
└── Dépendances (AWS SDK, Quarkus libs, etc.)

Taille: ~15-20 MB
```

### Mode Native (pour Lambda en production)
```
target/function.zip
│
├── bootstrap              # Script de démarrage Lambda
└── quarkus-iot-lambda-1.0.0-SNAPSHOT-runner  # Binaire natif

Taille: ~50-80 MB (beaucoup plus performant!)
Cold start: ~2-3 secondes
```

## 🔨 Commandes de build

### Build JVM (rapide, pour tester)
```bash
mvn clean package -DskipTests
# Résultat: target/quarkus-iot-lambda-1.0.0-SNAPSHOT-runner.jar
```

### Build Native (pour production Lambda)
```bash
./build.sh
# ou
mvn clean package -Pnative -Dquarkus.native.container-build=true
# Résultat: target/function.zip
```

## 📋 Dépendances packagées

Les principales dépendances incluses :
- ✅ Quarkus Core (Arc CDI)
- ✅ Quarkus RESTEasy Reactive (JAX-RS)
- ✅ AWS Lambda Runtime
- ✅ AWS DynamoDB SDK Enhanced Client
- ✅ Jackson (JSON)
- ✅ Micrometer (Métriques)
- ✅ SmallRye Health
- ✅ SmallRye OpenAPI

## 🚀 Test du package

### Mode JVM local
```bash
# Build
mvn clean package -DskipTests

# Run localement
java -jar target/quarkus-iot-lambda-1.0.0-SNAPSHOT-runner.jar

# Ou avec Quarkus dev mode (hot reload)
mvn quarkus:dev
```

### Mode Native (simulation Lambda)
```bash
# Build natif
./build.sh

# Test avec SAM CLI local
sam local start-api
```

## 📊 Comparaison des tailles

| Type | Taille | Cold Start | Usage |
|------|--------|------------|-------|
| **JVM** | ~15-20 MB | ~10-15s | Dev/Test |
| **Native** | ~50-80 MB | ~2-3s | Production Lambda |
| **Spring Boot** | ~80-150 MB | ~15-20s | Ancien (ECS) |

## 🎯 Pour votre projet Terraform

Le package à déployer sera :
- **Fichier** : `target/function.zip` (après `./build.sh`)
- **Runtime** : `provided.al2` (custom runtime pour native)
- **Handler** : `io.quarkus.amazon.lambda.runtime.QuarkusStreamHandler`
- **Architecture** : `x86_64`
- **Memory** : 512 MB recommandé
- **Timeout** : 30 secondes recommandé

### Exemple de ressource Terraform

```hcl
resource "aws_lambda_function" "quarkus_iot" {
  filename         = "target/function.zip"
  function_name    = "quarkus-iot-lambda"
  role            = aws_iam_role.lambda_role.arn
  handler         = "not.used.in.provided.runtime"
  runtime         = "provided.al2"
  architectures   = ["x86_64"]
  memory_size     = 512
  timeout         = 30

  environment {
    variables = {
      QUARKUS_LAMBDA_HANDLER = "io.quarkus.amazon.lambda.runtime.QuarkusStreamHandler"
      DYNAMODB_SENSOR_DATA_TABLE_NAME = aws_dynamodb_table.sensor_data.name
      DYNAMODB_RUNS_TABLE_NAME = aws_dynamodb_table.runs.name
    }
  }
}
```
