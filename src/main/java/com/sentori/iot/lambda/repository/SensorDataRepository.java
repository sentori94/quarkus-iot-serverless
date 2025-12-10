package com.sentori.iot.lambda.repository;

import com.sentori.iot.lambda.model.SensorData;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Repository pour SensorData
 * Équivalent de com.sentori.iot.repository.SensorDataRepository
 */
@ApplicationScoped
public class SensorDataRepository {

    @Inject
    DynamoDbEnhancedClient dynamoDbEnhancedClient;

    @ConfigProperty(name = "dynamodb.sensor-data.table-name")
    String tableName;

    private DynamoDbTable<SensorData> getTable() {
        return dynamoDbEnhancedClient.table(tableName, TableSchema.fromBean(SensorData.class));
    }

    public SensorData save(SensorData sensorData) {
        if (sensorData.getTimestamp() == null) {
            sensorData.setTimestamp(Instant.now());
        }
        getTable().putItem(sensorData);
        return sensorData;
    }

    public List<SensorData> findBySensorId(String sensorId) {
        QueryConditional queryConditional = QueryConditional.keyEqualTo(
                Key.builder().partitionValue(sensorId).build()
        );
        
        return getTable()
                .query(QueryEnhancedRequest.builder()
                        .queryConditional(queryConditional)
                        .scanIndexForward(false)
                        .limit(100)
                        .build())
                .items()
                .stream()
                .collect(Collectors.toList());
    }

    public List<SensorData> findBySensorIdAndTimeRange(String sensorId, Instant startTime, Instant endTime) {
        QueryConditional queryConditional = QueryConditional
                .sortBetween(
                        Key.builder().partitionValue(sensorId).sortValue(startTime.toString()).build(),
                        Key.builder().partitionValue(sensorId).sortValue(endTime.toString()).build()
                );
        
        return getTable()
                .query(QueryEnhancedRequest.builder()
                        .queryConditional(queryConditional)
                        .scanIndexForward(false)
                        .build())
                .items()
                .stream()
                .collect(Collectors.toList());
    }
}
