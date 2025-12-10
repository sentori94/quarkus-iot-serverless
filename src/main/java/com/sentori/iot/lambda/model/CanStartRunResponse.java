package com.sentori.iot.lambda.model;

/**
 * Response pour vérifier si on peut démarrer un run
 * Équivalent de com.sentori.iot.model.CanStartRunResponse
 */
public class CanStartRunResponse {
    private boolean canStart;
    private long currentRunning;
    private int maxAllowed;
    private long available;
    
    public CanStartRunResponse() {
    }
    
    public CanStartRunResponse(boolean canStart, long currentRunning, int maxAllowed, long available) {
        this.canStart = canStart;
        this.currentRunning = currentRunning;
        this.maxAllowed = maxAllowed;
        this.available = available;
    }
    
    public boolean isCanStart() {
        return canStart;
    }
    
    public void setCanStart(boolean canStart) {
        this.canStart = canStart;
    }
    
    public long getCurrentRunning() {
        return currentRunning;
    }
    
    public void setCurrentRunning(long currentRunning) {
        this.currentRunning = currentRunning;
    }
    
    public int getMaxAllowed() {
        return maxAllowed;
    }
    
    public void setMaxAllowed(int maxAllowed) {
        this.maxAllowed = maxAllowed;
    }
    
    public long getAvailable() {
        return available;
    }
    
    public void setAvailable(long available) {
        this.available = available;
    }
}
