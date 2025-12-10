package com.sentori.iot.lambda.model;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import java.time.Instant;

/**
 * Modèle SensorData - Migration depuis Spring Boot JPA vers DynamoDB
 * Équivalent de com.sentori.iot.model.SensorData
 */
@DynamoDbBean
public class SensorData {
    
    private String sensorId;
    private Instant timestamp;
    private String type;
    private Double reading;
    private String runId; // Lien avec le run pour faciliter les requêtes
    
    public SensorData() {
    }
    
    public SensorData(String sensorId, String type, Double reading) {
        this.sensorId = sensorId;
        this.type = type;
        this.reading = reading;
        this.timestamp = Instant.now();
    }
    
    @DynamoDbPartitionKey
    @DynamoDbAttribute("sensorId")
    public String getSensorId() {
        return sensorId;
    }
    
    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }
    
    @DynamoDbSortKey
    @DynamoDbAttribute("timestamp")
    public Instant getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
    
    @DynamoDbAttribute("type")
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    @DynamoDbAttribute("reading")
    public Double getReading() {
        return reading;
    }
    
    public void setReading(Double reading) {
        this.reading = reading;
    }
    
    @DynamoDbAttribute("runId")
    public String getRunId() {
        return runId;
    }
    
    public void setRunId(String runId) {
        this.runId = runId;
    }
}
