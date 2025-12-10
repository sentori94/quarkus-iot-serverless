package com.sentori.iot.lambda.service;

import com.sentori.iot.lambda.model.SensorData;
import com.sentori.iot.lambda.repository.SensorDataRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.time.Instant;
import java.util.List;

/**
 * Service pour la gestion des données de capteurs
 * Équivalent de com.sentori.iot.service.SensorService
 */
@ApplicationScoped
public class SensorService {

    private static final Logger LOG = Logger.getLogger(SensorService.class);

    @Inject
    SensorDataRepository repository;

    public SensorData save(SensorData data) {
        LOG.infof("Saving sensor data: sensorId=%s, type=%s, reading=%s", 
                  data.getSensorId(), data.getType(), data.getReading());
        return repository.save(data);
    }

    public List<SensorData> findBySensorId(String sensorId) {
        LOG.infof("Finding sensor data for sensorId: %s", sensorId);
        return repository.findBySensorId(sensorId);
    }

    public List<SensorData> findBySensorIdAndTimeRange(String sensorId, Instant startTime, Instant endTime) {
        LOG.infof("Finding sensor data for sensorId: %s between %s and %s", sensorId, startTime, endTime);
        return repository.findBySensorIdAndTimeRange(sensorId, startTime, endTime);
    }
}
