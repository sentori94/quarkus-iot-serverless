# Guide de migration complet - Spring Boot vers Quarkus Lambda

## 📋 Table des matières

1. [Vue d'ensemble](#vue-densemble)
2. [Changements d'architecture](#changements-darchitecture)
3. [Migration du code](#migration-du-code)
4. [Déploiement](#déploiement)
5. [Tests](#tests)

## Vue d'ensemble

Cette migration transforme l'application `iot-playground-starter` de :
- **Spring Boot 3.x** sur **ECS Fargate**
- Vers **Quarkus 3.x** sur **AWS Lambda**

## Changements d'architecture

### Base de données

**PostgreSQL → DynamoDB**

| PostgreSQL (JPA) | DynamoDB (Enhanced Client) |
|------------------|---------------------------|
| Tables relationnelles | Tables NoSQL |
| `@Entity` | `@DynamoDbBean` |
| `@Id` | `@DynamoDbPartitionKey` |
| Auto-increment ID | UUID ou composite key |

### Exemple de migration de modèle

**Avant (Spring Boot)** :
```java
@Entity
public class SensorData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String sensorId;
    private String type;
    private double reading;
    private LocalDateTime timestamp;
}
```

**Après (Quarkus Lambda)** :
```java
@DynamoDbBean
public class SensorData {
    @DynamoDbPartitionKey
    private String sensorId;
    
    @DynamoDbSortKey
    private Instant timestamp;
    
    private String type;
    private Double reading;
}
```

### Controllers → Resources

**Avant (Spring Boot)** :
```java
@RestController
@RequestMapping("/sensors")
public class SensorController {
    @Autowired
    private SensorService sensorService;
    
    @PostMapping("/data")
    public ResponseEntity<SensorData> ingestData(@RequestBody SensorData data) {
        return ResponseEntity.ok(sensorService.save(data));
    }
}
```

**Après (Quarkus)** :
```java
@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {
    @Inject
    SensorService sensorService;
    
    @POST
    @Path("/data")
    public Response ingestData(SensorData data) {
        return Response.ok(sensorService.save(data)).build();
    }
}
```

## Tests de compatibilité

### Endpoints à tester après déploiement

```bash
# Health check
curl https://YOUR_API_GATEWAY_URL/api/health

# Ingest sensor data
curl -X POST https://YOUR_API_GATEWAY_URL/sensors/data \
  -H "Content-Type: application/json" \
  -d '{
    "sensorId": "SENSOR-001",
    "type": "temperature",
    "reading": 23.5
  }'

# Start run
curl -X POST https://YOUR_API_GATEWAY_URL/api/runs/start \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "sensorIds": ["SENSOR-001", "SENSOR-002"],
    "duration": 60,
    "interval": 1000
  }'
```

## Performance comparée

| Métrique | Spring Boot ECS | Quarkus Lambda |
|----------|----------------|----------------|
| Démarrage | ~10s | ~2-3s (cold start) |
| Mémoire idle | 512MB-2GB | 0MB (pas d'idle) |
| Coût mensuel (faible trafic) | $30-50 | $5-10 |
| Coût mensuel (trafic élevé) | $50-100 | $20-50 |

## Checklist de migration

- [x] Modèles de données migrés vers DynamoDB
- [x] Controllers migrés vers Resources JAX-RS
- [x] Services adaptés (injection CDI)
- [x] Repositories DynamoDB créés
- [x] Configuration externalisée
- [x] Scripts de build et déploiement
- [ ] Tests de charge
- [ ] Migration des données historiques
- [ ] Mise à jour de la CI/CD

## Points d'attention

1. **DynamoDB Design** : Les clés de partition/tri doivent être bien pensées pour la performance
2. **Cold starts** : Lambda peut avoir des cold starts, prévoir un warming si critique
3. **Timeouts** : Lambda a un timeout max de 15 minutes
4. **Concurrent executions** : Par défaut 1000, peut être augmenté

## Rollback

En cas de problème, l'ancien système Spring Boot ECS reste déployé. 
Pour rollback :

```bash
# Supprimer le stack Lambda
sam delete --stack-name quarkus-iot-lambda

# Rediriger le trafic vers l'ancien ALB ECS
```
