package com.sentori.iot.lambda.resource;

import com.sentori.iot.lambda.model.CanStartRunResponse;
import com.sentori.iot.lambda.model.RunEntity;
import com.sentori.iot.lambda.model.RunStartRequest;
import com.sentori.iot.lambda.model.RunStartResponse;
import com.sentori.iot.lambda.service.RunService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Endpoints REST pour les runs
 * Équivalent de com.sentori.iot.controller.RunController
 */
@Path("/api/runs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Runs", description = "Simulation run management endpoints")
public class RunResource {

    private static final Logger LOG = Logger.getLogger(RunResource.class);
    private static final DateTimeFormatter RUN_ID_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

    @Inject
    RunService runService;

    @ConfigProperty(name = "app.run.max-concurrent", defaultValue = "5")
    int maxConcurrentRuns;

    /**
     * GET /api/runs - Liste tous les runs
     */
    @GET
    @Operation(summary = "List all runs", description = "Get all simulation runs")
    public List<RunEntity> listRuns() {
        return runService.getAllRuns();
    }

    /**
     * GET /api/runs/{id} - Récupère un run par son ID
     */
    @GET
    @Path("/{id}")
    @Operation(summary = "Get run by ID", description = "Retrieve a specific run by its ID")
    public Response getRun(@PathParam("id") String id) {
        return runService.getRunById(id)
                .map(run -> Response.ok(run).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    /**
     * GET /api/runs/running - Récupère les runs en cours
     */
    @GET
    @Path("/running")
    @Operation(summary = "Get running runs", description = "Get all currently running simulations")
    public List<RunEntity> getRunningRuns() {
        return runService.getRunningRuns();
    }

    /**
     * GET /api/runs/can-start - Vérifie si on peut démarrer un nouveau run
     */
    @GET
    @Path("/can-start")
    @Operation(summary = "Check if can start run", description = "Check if a new simulation can be started")
    public Response canStartRun() {
        long currentRunning = runService.countRunning();
        boolean canStart = currentRunning < maxConcurrentRuns;
        long available = maxConcurrentRuns - currentRunning;
        
        CanStartRunResponse response = new CanStartRunResponse(
            canStart, currentRunning, maxConcurrentRuns, available
        );
        
        if (canStart) {
            return Response.ok(response).build();
        } else {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(response).build();
        }
    }

    /**
     * POST /api/runs/start - Démarre un nouveau run
     */
    @POST
    @Path("/start")
    @Operation(summary = "Start new run", description = "Start a new simulation run")
    public Response startRun(
            RunStartRequest request,
            @HeaderParam("X-User") @DefaultValue("anonymous") String user) {
        
        try {
            RunEntity run = new RunEntity();
            run.setUsername(request.getUsername() != null ? request.getUsername() : user);
            
            // Construire les params
            Map<String, Object> params = new HashMap<>();
            params.put("source", "api");
            params.put("sensorIds", request.getSensorIds());
            params.put("duration", request.getDuration());
            params.put("interval", request.getInterval());
            run.setParams(params);
            
            RunEntity savedRun = runService.startRun(run);
            
            RunStartResponse response = new RunStartResponse(
                savedRun.getId(), 
                savedRun.getGrafanaUrl()
            );
            
            return Response.status(Response.Status.CREATED).entity(response).build();
            
        } catch (IllegalStateException e) {
            LOG.error("Cannot start run: " + e.getMessage());
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    /**
     * POST /api/runs/{id}/finish - Termine un run
     */
    @POST
    @Path("/{id}/finish")
    @Operation(summary = "Finish run", description = "Mark a run as finished")
    public Response finishRun(
            @PathParam("id") String id,
            @QueryParam("status") @DefaultValue("SUCCESS") String status,
            @QueryParam("errorMessage") String errorMessage) {
        
        try {
            RunEntity run = runService.finishRun(id, status, errorMessage);
            return Response.ok(run).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    /**
     * POST /api/runs/interrupt-all - Interrompt tous les runs en cours
     */
    @POST
    @Path("/interrupt-all")
    @Operation(summary = "Interrupt all runs", description = "Cancel all running simulations")
    public Response interruptAll() {
        long beforeCount = runService.countRunning();
        runService.interruptAllRunning();
        long afterCount = runService.countRunning();
        
        return Response.ok(Map.of(
            "message", "All running runs interrupted",
            "interrupted", beforeCount - afterCount
        )).build();
    }
}
