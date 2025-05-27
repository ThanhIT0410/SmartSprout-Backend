package org.smartsproutbackend.service;

import org.smartsproutbackend.entity.WateringLog;
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

    public void triggerWatering(String deviceId, String deviceName, int duration) {
        try {
            String topic = "watering/" + deviceId;
            String startPayload = "{\"action\":\"start\"}";
            mqttClientSingleton.publishToTopic(topic, startPayload);
            logWatering(deviceId, deviceName, duration);
            new Thread(() -> {
                try {
                    Thread.sleep(duration * 60_000L);
                    String endPayload = "{\"action\":\"end\"}";
                    mqttClientSingleton.publishToTopic(topic, endPayload);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (Exception e) {
            throw new RuntimeException("Error triggering watering", e);
        }
    }

    private void logWatering(String deviceId, String deviceName, int duration) {
        WateringLog wateringLog = new WateringLog();
        wateringLog.setDeviceId(deviceId);
        wateringLog.setDeviceName(deviceName);
        wateringLog.setDuration(duration);
        wateringLog.setExecuteTime(LocalDateTime.now());
        wateringLogRepository.save(wateringLog);
    }

}
