package org.smartsproutbackend.statemanager;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WateringStateManager {

    private final Map<String, LocalDateTime> wateringStates = new ConcurrentHashMap<>();

    public boolean isExecuting(String deviceId) {
        return wateringStates.containsKey(deviceId) && LocalDateTime.now().isBefore(wateringStates.get(deviceId));
    }

    public void markAsExecuting(String deviceId, int duration) {
        LocalDateTime endTime = LocalDateTime.now().plusSeconds(duration);
        wateringStates.put(deviceId, endTime);
    }

    public LocalDateTime getEndTime(String deviceId) {
        return wateringStates.get(deviceId);
    }
}
