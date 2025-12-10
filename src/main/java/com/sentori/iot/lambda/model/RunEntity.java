package com.sentori.iot.lambda.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Modèle RunEntity - Migration depuis Spring Boot JPA vers DynamoDB
 * Équivalent de com.sentori.iot.model.run.RunEntity
 */
@DynamoDbBean
public class RunEntity {
    
    private String id;
    private String username;
    private String status; // RUNNING, SUCCESS, FAILED, CANCELED, TIMEOUT
    private Instant startedAt;
    private Instant finishedAt;
    private Map<String, Object> params;
    private String errorMessage;
    private String grafanaUrl;
    
    public RunEntity() {
        this.id = UUID.randomUUID().toString();
        this.startedAt = Instant.now();
    }
    
    @DynamoDbPartitionKey
    @DynamoDbAttribute("id")
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    @DynamoDbAttribute("username")
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    @DynamoDbAttribute("status")
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    @DynamoDbAttribute("startedAt")
    public Instant getStartedAt() {
        return startedAt;
    }
    
    public void setStartedAt(Instant startedAt) {
        this.startedAt = startedAt;
    }
    
    @DynamoDbAttribute("finishedAt")
    public Instant getFinishedAt() {
        return finishedAt;
    }
    
    public void setFinishedAt(Instant finishedAt) {
        this.finishedAt = finishedAt;
    }
    
    @DynamoDbAttribute("params")
    public Map<String, Object> getParams() {
        return params;
    }
    
    public void setParams(Map<String, Object> params) {
        this.params = params;
    }
    
    @DynamoDbAttribute("errorMessage")
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    @DynamoDbAttribute("grafanaUrl")
    public String getGrafanaUrl() {
        return grafanaUrl;
    }
    
    public void setGrafanaUrl(String grafanaUrl) {
        this.grafanaUrl = grafanaUrl;
    }
}
