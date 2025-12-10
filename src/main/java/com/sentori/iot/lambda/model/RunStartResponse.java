package com.sentori.iot.lambda.model;

/**
 * Response pour le démarrage d'un run
 * Équivalent de com.sentori.iot.model.RunStartResponse
 */
public class RunStartResponse {
    private String runId;
    private String grafanaUrl;
    
    public RunStartResponse() {
    }
    
    public RunStartResponse(String runId, String grafanaUrl) {
        this.runId = runId;
        this.grafanaUrl = grafanaUrl;
    }
    
    public String getRunId() {
        return runId;
    }
    
    public void setRunId(String runId) {
        this.runId = runId;
    }
    
    public String getGrafanaUrl() {
        return grafanaUrl;
    }
    
    public void setGrafanaUrl(String grafanaUrl) {
        this.grafanaUrl = grafanaUrl;
    }
}
