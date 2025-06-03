package org.smartsproutbackend.service;

import org.smartsproutbackend.entity.WateringLog;
import org.smartsproutbackend.enums.WateringOperation;
import org.smartsproutbackend.mqtt.MqttClientSingleton;
import org.smartsproutbackend.repository.WateringLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class WateringService {

    @Autowired
    private WateringLogRepository wateringLogRepository;

    @Autowired
    private MqttClientSingleton mqttClientSingleton;

    public void startWatering(String deviceId, String deviceName, int duration) {
        try {
            String topic = "watering/" + deviceId;
            String startPayload = String.format("{\"action\":\"start\", \"duration\": %d}", duration);
            mqttClientSingleton.publishToTopic(topic, startPayload);
            logWatering(deviceId, deviceName, WateringOperation.START, duration);
        } catch (Exception e) {
            throw new RuntimeException("Error triggering watering", e);
        }
    }

    public void stopWatering(String deviceId, String deviceName) {
        try {
            String topic = "watering/" + deviceId;
            String stopPayload = "{\"action\":\"stop\"}";
            mqttClientSingleton.publishToTopic(topic, stopPayload);
            logWatering(deviceId, deviceName, WateringOperation.STOP, 0);
        } catch (Exception e) {
            throw new RuntimeException("Error triggering watering", e);
        }
    }

    private void logWatering(String deviceId, String deviceName, WateringOperation operation, int duration) {
        WateringLog wateringLog = new WateringLog();
        wateringLog.setDeviceId(deviceId);
        wateringLog.setDeviceName(deviceName);
        wateringLog.setOperation(operation);
        wateringLog.setExecuteTime(LocalDateTime.now());
        wateringLog.setDuration(duration);
        wateringLogRepository.save(wateringLog);
    }
}
