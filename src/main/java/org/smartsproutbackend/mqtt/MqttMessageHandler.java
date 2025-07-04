package org.smartsproutbackend.mqtt;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.smartsproutbackend.entity.RecentMessage;
import org.smartsproutbackend.repository.RecentMessageRepository;
import org.smartsproutbackend.service.WebSocketPushService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.*;
import java.util.*;

@Component
public class MqttMessageHandler implements MqttCallback {

    @Autowired
    private RecentMessageRepository recentMessageRepository;

    @Autowired
    private WebSocketPushService webSocketPushService;

    private final int MESSAGE_INTERVAL = 60_000;

    @Override
    public void messageArrived(String topic, MqttMessage message) {
        String payload = new String(message.getPayload(), StandardCharsets.UTF_8);

        RecentMessage msg;
        try {
            msg = parseMessage(topic, payload);
        } catch (Exception e) {
            return;
        }

        webSocketPushService.sendToTopic(topic, payload);

        if (Duration.between(msg.getTimestamp(), LocalDateTime.now()).toMillis() > MESSAGE_INTERVAL) return;

        recentMessageRepository.save(msg);
    }

    private RecentMessage parseMessage(String topic, String json) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Number> data = mapper.readValue(json, new TypeReference<>() {});
        float air = data.getOrDefault("air", 0).floatValue();
        float temp = data.getOrDefault("temp", 0).floatValue();
        float soil = data.getOrDefault("soil", 0).floatValue();

        LocalDateTime now = LocalDateTime.now();
        int roundedHour = (now.getHour() / 2) * 2;
        LocalDateTime timestamp = LocalDateTime.of(now.toLocalDate(), LocalTime.of(roundedHour, 0));

        RecentMessage msg = new RecentMessage();
        msg.setTopic(topic);
        msg.setAir(air);
        msg.setTemp(temp);
        msg.setSoil(soil);
        msg.setTimestamp(timestamp);
        return msg;
    }

    @Override
    public void connectionLost(Throwable cause) {}

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {}
}
