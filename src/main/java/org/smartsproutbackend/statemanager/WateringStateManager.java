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

    public synchronized boolean tryMarkAsExecuting(String deviceId, int duration) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime existingEnd = wateringStates.get(deviceId);
        System.out.println("It is now: " + now
                + "; Last endtime of this device: " + existingEnd
                + "; State of device " + deviceId + " : " + ((existingEnd != null && now.isBefore(existingEnd)) ? "not executing" : "executing"));
        if (existingEnd != null && now.isBefore(existingEnd)) {
            return false;
        }
        LocalDateTime endTime = now.plusSeconds(duration);
        wateringStates.put(deviceId, endTime);
        return true;
    }

    public LocalDateTime getEndTime(String deviceId) {
        return wateringStates.get(deviceId);
    }
}
