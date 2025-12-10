package com.sentori.iot.lambda.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.Map;

/**
 * Endpoint de health check
 */
@Path("/api/health")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Health", description = "Health check endpoint")
public class HealthResource {

    @GET
    @Operation(summary = "Health check", description = "Check if the application is running")
    public Map<String, String> health() {
        return Map.of(
            "status", "UP",
            "service", "quarkus-iot-lambda",
            "version", "1.0.0"
        );
    }
}
