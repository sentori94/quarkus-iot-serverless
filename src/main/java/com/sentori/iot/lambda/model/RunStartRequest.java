package com.sentori.iot.lambda.model;

/**
 * Request pour démarrer un run
 * Équivalent de com.sentori.iot.model.RunStartRequest
 */
public class RunStartRequest {
    private String username;
    private java.util.List<String> sensorIds;
    private Integer duration;
    private Long interval;
    
    public RunStartRequest() {
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public java.util.List<String> getSensorIds() {
        return sensorIds;
    }
    
    public void setSensorIds(java.util.List<String> sensorIds) {
        this.sensorIds = sensorIds;
    }
    
    public Integer getDuration() {
        return duration;
    }
    
    public void setDuration(Integer duration) {
        this.duration = duration;
    }
    
    public Long getInterval() {
        return interval;
    }
    
    public void setInterval(Long interval) {
        this.interval = interval;
    }
}
