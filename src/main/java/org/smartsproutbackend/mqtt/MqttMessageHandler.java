package org.smartsproutbackend.mqtt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.smartsproutbackend.service.WebSocketPushService;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class MqttMessageHandler implements MqttCallback {

    private final Map<String, Deque<ParsedMessage>> latestMessages = new ConcurrentHashMap<>();

    private final WebSocketPushService webSocketPushService;

    private final int MAXIMUM_MESSAGES = 10;
    private final int MESSAGE_TIME_LIMIT = 10_800_000; // 3 giờ

    public MqttMessageHandler(WebSocketPushService webSocketPushService) {
        this.webSocketPushService = webSocketPushService;
        startCleanupTask();
    }

    private static class ParsedMessage {
        public LocalDateTime timestamp;
        public int temperature;
        public int humidity;
        public int haha;

        public ParsedMessage(String json) throws IOException {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Integer> data = mapper.readValue(json, new TypeReference<>() {});
            int haha = data.getOrDefault("haha", 0);
            int temperature = data.getOrDefault("temperature", 0);
            int humidity = data.getOrDefault("humidity", 0);
            this.temperature = temperature;
            this.humidity = humidity;
            this.haha = haha;
            this.timestamp = LocalDateTime.now();
        }

        public boolean isExpired(long timeLimit) {
            long now = System.currentTimeMillis();
            long tsMillis = timestamp.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            return now - tsMillis > timeLimit;
        }
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String payload = new String(message.getPayload(), StandardCharsets.UTF_8);

        ParsedMessage parsedMessage;
        try {
            parsedMessage = new ParsedMessage(payload);
        } catch (Exception e) {
            return;
        }

        webSocketPushService.sendToTopic(topic, payload);

        LocalDateTime now = parsedMessage.timestamp;
        int roundedHours = (now.getHour() / 3) * 3;
        LocalDateTime rounded = LocalDateTime.of(now.toLocalDate(), LocalTime.of(roundedHours, 0));
        long diffMillis = Duration.between(rounded, now).toMillis();
        if (diffMillis > 60_000) return;

        latestMessages.putIfAbsent(topic, new LinkedList<>());
        Deque<ParsedMessage> queue = latestMessages.get(topic);
        synchronized (queue) {
            while (!queue.isEmpty() && queue.peekFirst().isExpired(MESSAGE_TIME_LIMIT)) {
                queue.pollFirst();
            }
            if (queue.size() >= MAXIMUM_MESSAGES) {
                queue.pollFirst();
            }
            queue.offerLast(parsedMessage);
        }
    }

    public List<Map<String, Object>> getRecentMessages(String topic) {
        Deque<ParsedMessage> queue = latestMessages.getOrDefault(topic, new LinkedList<>());
        synchronized (queue) {
            return queue.stream()
                    .filter(msg -> msg != null && !msg.isExpired(MESSAGE_TIME_LIMIT))
                    .map(msg -> {
                        Map<String, Object> result = new HashMap<>();
                        result.put("temperature", msg.temperature);
                        result.put("humidity", msg.humidity);
                        result.put("haha", msg.haha);
                        result.put("timestamp", msg.timestamp.toString());
                        return result;
                    })
                    .collect(Collectors.toList());
        }
    }

    private void startCleanupTask() {
        Thread cleaner = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(300_000);
                    for (Deque<ParsedMessage> queue : latestMessages.values()) {
                        synchronized (queue) {
                            while (!queue.isEmpty() && queue.peekFirst().isExpired(MESSAGE_TIME_LIMIT)) {
                                queue.pollFirst();
                            }
                        }
                    }
                } catch (InterruptedException ignored) {}
            }
        });

        cleaner.setDaemon(true);
        cleaner.start();
    }

    @Override
    public void connectionLost(Throwable cause) {}

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {}
}
