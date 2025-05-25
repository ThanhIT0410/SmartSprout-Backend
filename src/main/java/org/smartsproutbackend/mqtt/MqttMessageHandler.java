package org.smartsproutbackend.mqtt;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.smartsproutbackend.entity.DataRecord;
import org.smartsproutbackend.repository.DataRecordRepository;
import org.smartsproutbackend.service.WebSocketPushService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MqttMessageHandler implements MqttCallback {

    private final Map<String, Deque<String>> latestMessages = new ConcurrentHashMap<>();

    private final WebSocketPushService webSocketPushService;

    private final int MAXIMUM_MESSAGES = 10;

    public MqttMessageHandler(WebSocketPushService webSocketPushService) {
        this.webSocketPushService = webSocketPushService;
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String payload = new String(message.getPayload(), StandardCharsets.UTF_8);

        latestMessages.putIfAbsent(topic, new LinkedList<>());
        Deque<String> queue = latestMessages.get(topic);
        synchronized (queue) {
            if (queue.size() >= MAXIMUM_MESSAGES) queue.pollFirst();
            queue.offerLast(payload);
        }

        webSocketPushService.sendToTopic(topic, payload);
    }

    public List<String> getRecentMessages(String topic) {
        Deque<String> queue = latestMessages.getOrDefault(topic, new LinkedList<>());
        synchronized (queue) {
            return new ArrayList<>(queue);
        }
    }

    @Override
    public void connectionLost(Throwable cause) {}

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {}
}
