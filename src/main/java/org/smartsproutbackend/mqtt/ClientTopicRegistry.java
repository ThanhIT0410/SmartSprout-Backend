package org.smartsproutbackend.mqtt;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClientTopicRegistry {
    private static final Map<String, String> topicToClients = new ConcurrentHashMap<>();

    public static void registerTopic(String clientId, String topic) {
        topicToClients.put(topic, clientId);
    }

    public static void unregisterTopic(String topic) {
        topicToClients.remove(topic);
    }

    public static String getClientForTopic(String topic) {
        return topicToClients.get(topic);
    }

    public static boolean isTopicRegistered(String topic) {
        return topicToClients.containsKey(topic);
    }
}
