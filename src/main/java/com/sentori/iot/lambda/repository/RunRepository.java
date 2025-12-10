package com.sentori.iot.lambda.repository;

import com.sentori.iot.lambda.model.RunEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Repository pour RunEntity
 * Équivalent de com.sentori.iot.repository.RunRepository
 */
@ApplicationScoped
public class RunRepository {

    @Inject
    DynamoDbEnhancedClient dynamoDbEnhancedClient;

    @ConfigProperty(name = "dynamodb.runs.table-name")
    String tableName;

    private DynamoDbTable<RunEntity> getTable() {
        return dynamoDbEnhancedClient.table(tableName, TableSchema.fromBean(RunEntity.class));
    }

    public RunEntity save(RunEntity run) {
        if (run.getStartedAt() == null) {
            run.setStartedAt(Instant.now());
        }
        getTable().putItem(run);
        return run;
    }

    public Optional<RunEntity> findById(String id) {
        Key key = Key.builder()
                .partitionValue(id)
                .build();
        RunEntity run = getTable().getItem(key);
        return Optional.ofNullable(run);
    }

    public List<RunEntity> findAll() {
        PageIterable<RunEntity> pages = getTable().scan(ScanEnhancedRequest.builder().build());
        return pages.items().stream()
                .sorted((r1, r2) -> r2.getStartedAt().compareTo(r1.getStartedAt()))
                .collect(Collectors.toList());
    }

    public List<RunEntity> findTop10ByOrderByStartedAtDesc() {
        return findAll().stream()
                .limit(10)
                .collect(Collectors.toList());
    }

    public boolean existsRunning() {
        return countRunning() > 0;
    }

    public long countRunning() {
        Map<String, AttributeValue> expressionValues = new HashMap<>();
        expressionValues.put(":status", AttributeValue.builder().s("RUNNING").build());
        
        PageIterable<RunEntity> pages = getTable().scan(ScanEnhancedRequest.builder()
                .filterExpression(software.amazon.awssdk.services.dynamodb.model.AttributeValue.builder()
                        .s("status = :status")
                        .build()
                        .toString())
                .build());
        
        return pages.items().stream()
                .filter(run -> "RUNNING".equals(run.getStatus()))
                .count();
    }

    public List<RunEntity> findRunning() {
        PageIterable<RunEntity> pages = getTable().scan(ScanEnhancedRequest.builder().build());
        return pages.items().stream()
                .filter(run -> "RUNNING".equals(run.getStatus()))
                .sorted((r1, r2) -> r2.getStartedAt().compareTo(r1.getStartedAt()))
                .collect(Collectors.toList());
    }

    public void delete(String id) {
        Key key = Key.builder()
                .partitionValue(id)
                .build();
        getTable().deleteItem(key);
    }

    public RunEntity update(RunEntity run) {
        getTable().updateItem(run);
        return run;
    }
}
