package com.sentori.iot.lambda.resource;

import com.sentori.iot.lambda.model.SensorData;
import com.sentori.iot.lambda.service.SensorService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.time.Instant;
import java.util.List;

/**
 * Endpoints REST pour les capteurs
 * Équivalent de com.sentori.iot.controller.SensorController
 */
@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Sensors", description = "Sensor data management endpoints")
public class SensorResource {

    @Inject
    SensorService sensorService;

    @Inject
    MeterRegistry meterRegistry;

    private Counter getIngestCounter() {
        return meterRegistry.counter("sensor.data.ingested");
    }

    /**
     * POST /sensors/data - Ingestion des données de capteur
     * Endpoint principal appelé par le SimulatorService
     */
    @POST
    @Path("/data")
    @Operation(summary = "Ingest sensor data", description = "Receive and store sensor data from IoT devices")
    public Response ingestData(SensorData sensorData) {
        SensorData saved = sensorService.save(sensorData);
        getIngestCounter().increment();
        return Response.status(Response.Status.CREATED).entity(saved).build();
    }

    /**
     * GET /sensors/{sensorId}/data - Récupérer les données d'un capteur
     */
    @GET
    @Path("/{sensorId}/data")
    @Operation(summary = "Get sensor data", description = "Retrieve historical data for a specific sensor")
    public List<SensorData> getSensorData(@PathParam("sensorId") String sensorId) {
        return sensorService.findBySensorId(sensorId);
    }

    /**
     * GET /sensors/{sensorId}/data/range - Récupérer les données sur une période
     */
    @GET
    @Path("/{sensorId}/data/range")
    @Operation(summary = "Get sensor data by time range", description = "Retrieve sensor data within a specific time range")
    public List<SensorData> getSensorDataByTimeRange(
            @PathParam("sensorId") String sensorId,
            @QueryParam("startTime") String startTime,
            @QueryParam("endTime") String endTime) {
        
        Instant start = Instant.parse(startTime);
        Instant end = Instant.parse(endTime);
        
        return sensorService.findBySensorIdAndTimeRange(sensorId, start, end);
    }
}
