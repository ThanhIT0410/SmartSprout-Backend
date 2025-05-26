package org.smartsproutbackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketPushService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void sendToTopic(String topic, String payload) {
        messagingTemplate.convertAndSend("/topic/data/" + topic, payload);
    }
}
