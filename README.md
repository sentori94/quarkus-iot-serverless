# Quarkus IoT Lambda - Migration de Spring Boot ECS vers Serverless

Migration complète de l'application **iot-playground-starter** (Spring Boot sur ECS) vers **Quarkus sur AWS Lambda** pour un backend 100% serverless.

## 🎯 Objectifs de la migration

- ✅ **De ECS à Lambda** : Éliminer la gestion d'infrastructure
- ✅ **Performance** : Cold start ~2-3s avec Quarkus Native
- ✅ **Coût** : Pay-per-request au lieu d'instances toujours actives
- ✅ **Scalabilité** : Auto-scaling illimité avec Lambda
- ✅ **Compatibilité** : Conservation de la même API REST

## 🏗️ Architecture

### Avant (Spring Boot + ECS)
```
Client → ALB → ECS (Spring Boot) → PostgreSQL
```

### Après (Quarkus + Lambda)
```
Client → API Gateway → Lambda (Quarkus Native) → DynamoDB
```

## 📦 Structure du projet

```
quarkus-iot-serverless/
├── src/main/java/com/sentori/iot/lambda/
│   ├── model/              # Modèles de données (DynamoDB)
│   │   ├── SensorData.java
│   │   ├── RunEntity.java
│   │   └── DTO classes...
│   ├── repository/         # Repositories DynamoDB
│   │   ├── SensorDataRepository.java
│   │   └── RunRepository.java
│   ├── service/           # Logique métier
│   │   ├── SensorService.java
│   │   └── RunService.java
│   ├── resource/          # REST endpoints
│   │   ├── SensorResource.java
│   │   ├── RunResource.java
│   │   └── HealthResource.java
│   └── util/              # Utilitaires
│       └── GrafanaUrlBuilder.java
├── src/main/resources/
│   └── application.properties
├── pom.xml
├── template.yaml          # AWS SAM CloudFormation
├── build.sh              # Script de build natif
└── deploy.sh             # Script de déploiement
```

## 🚀 Démarrage rapide

### Prérequis

- **Java 17+**
- **Maven 3.8+**
- **Docker** (pour build natif)
- **AWS CLI** configuré
- **AWS SAM CLI**

### Mode développement local

```bash
# Démarrer DynamoDB Local (optionnel)
docker run -p 8000:8000 amazon/dynamodb-local

# Lancer en mode dev avec hot reload
mvn quarkus:dev
```

L'application sera disponible sur `http://localhost:8080`

### Build natif pour Lambda

```bash
./build.sh
```

### Déploiement sur AWS

```bash
./deploy.sh
```

## 📡 API Endpoints

Tous les endpoints de l'application Spring Boot originale sont disponibles :

- **Sensors** : `/sensors/*` - Ingestion et récupération de données
- **Runs** : `/api/runs/*` - Gestion des simulations
- **Health** : `/api/health` - Health check

Documentation complète : `http://localhost:8080/swagger-ui`

## 🆚 Migration Spring Boot → Quarkus

| Aspect | Spring Boot | Quarkus Lambda |
|--------|-------------|----------------|
| **Base de données** | PostgreSQL (JPA) | DynamoDB (Enhanced Client) |
| **Runtime** | JVM sur ECS | GraalVM Native sur Lambda |
| **Coût** | ~$30-100/mois (24/7) | ~$5-20/mois (pay-per-use) |

Voir la documentation complète dans le README pour plus de détails sur la migration.
