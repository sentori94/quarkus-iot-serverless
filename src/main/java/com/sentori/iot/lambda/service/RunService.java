package com.sentori.iot.lambda.service;

import com.sentori.iot.lambda.model.RunEntity;
import com.sentori.iot.lambda.repository.RunRepository;
import com.sentori.iot.lambda.util.GrafanaUrlBuilder;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Service pour la gestion des runs
 * Équivalent de com.sentori.iot.service.RunService
 */
@ApplicationScoped
public class RunService {

    private static final Logger LOG = Logger.getLogger(RunService.class);

    @Inject
    RunRepository repository;

    @Inject
    GrafanaUrlBuilder grafanaUrlBuilder;

    @ConfigProperty(name = "app.run.max-concurrent", defaultValue = "5")
    int maxConcurrentRuns;

    public RunEntity startRun(RunEntity run) {
        LOG.infof("Starting run: id=%s, username=%s", run.getId(), run.getUsername());
        
        // Vérifier le nombre de runs en cours
        long runningCount = repository.countRunning();
        if (runningCount >= maxConcurrentRuns) {
            throw new IllegalStateException(
                String.format("Maximum concurrent runs reached (%d/%d)", runningCount, maxConcurrentRuns)
            );
        }
        
        run.setStatus("RUNNING");
        run.setStartedAt(Instant.now());
        
        // Construire l'URL Grafana si username est fourni
        if (run.getUsername() != null) {
            String grafanaUrl = grafanaUrlBuilder.buildUrl(run.getId(), run.getUsername());
            run.setGrafanaUrl(grafanaUrl);
        }
        
        return repository.save(run);
    }

    public RunEntity finishRun(String runId, String status, String errorMessage) {
        LOG.infof("Finishing run: id=%s, status=%s", runId, status);
        
        Optional<RunEntity> runOpt = repository.findById(runId);
        if (runOpt.isEmpty()) {
            throw new IllegalArgumentException("Run not found: " + runId);
        }
        
        RunEntity run = runOpt.get();
        run.setStatus(status);
        run.setFinishedAt(Instant.now());
        if (errorMessage != null) {
            run.setErrorMessage(errorMessage);
        }
        
        return repository.update(run);
    }

    public Optional<RunEntity> getRunById(String id) {
        return repository.findById(id);
    }

    public List<RunEntity> getAllRuns() {
        return repository.findAll();
    }

    public List<RunEntity> getRunningRuns() {
        return repository.findRunning();
    }

    public List<RunEntity> getTop10Runs() {
        return repository.findTop10ByOrderByStartedAtDesc();
    }

    public boolean canStartRun() {
        long runningCount = repository.countRunning();
        return runningCount < maxConcurrentRuns;
    }

    public long countRunning() {
        return repository.countRunning();
    }

    public void interruptAllRunning() {
        LOG.info("Interrupting all running runs");
        List<RunEntity> runningRuns = repository.findRunning();
        for (RunEntity run : runningRuns) {
            run.setStatus("CANCELED");
            run.setFinishedAt(Instant.now());
            run.setErrorMessage("Interrupted by user");
            repository.update(run);
        }
        LOG.infof("Interrupted %d runs", runningRuns.size());
    }
}
