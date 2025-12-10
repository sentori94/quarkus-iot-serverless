package com.sentori.iot.lambda.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

/**
 * Configuration CDI pour DynamoDB Enhanced Client
 */
@ApplicationScoped
public class DynamoDbConfig {

    @Inject
    DynamoDbClient dynamoDbClient;

    @Produces
    @ApplicationScoped
    public DynamoDbEnhancedClient dynamoDbEnhancedClient() {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
    }
}
