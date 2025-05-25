package org.smartsproutbackend.controller;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.smartsproutbackend.mqtt.MqttClientSingleton;
import org.smartsproutbackend.mqtt.MqttMessageHandler;
import org.smartsproutbackend.service.AccessControlService;
import org.smartsproutbackend.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/data-streaming")
public class DataStreamController {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private MqttClientSingleton mqttClientSingleton;

    @Autowired
    private MqttMessageHandler mqttMessageHandler;

    @Autowired
    private AccessControlService accessControlService;

    /**
     *
     * @param topic
     * @param authHeader request with header {"Autorization": 'Bearer ${token}'}
     * @return subscribe to topic
     * @throws MqttException
     */
    @PostMapping("/subscribe")
    public ResponseEntity<?> subscribe(@RequestParam String topic, @RequestHeader("Authorization") String authHeader) throws MqttException {
        String token = authHeader.replace("Bearer ", "");
        String username = tokenService.extractUsername(token);
        if (!accessControlService.userHasAccessToTopic(username, topic)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You do not have access to this topic");
        }
        mqttClientSingleton.subscribeToTopic(topic);
        return ResponseEntity.ok("Subscribed to topic: " + topic);
    }

    @PostMapping("/unsubscribe")
    public ResponseEntity<?> unsubscribe(@RequestParam String topic, @RequestHeader("Authorization") String authHeader) throws MqttException {
        String token = authHeader.replace("Bearer ", "");
        String username = tokenService.extractUsername(token);
        if (!accessControlService.userHasAccessToTopic(username, topic)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You do not have access to this topic");
        }
        mqttClientSingleton.unsubscribeToTopic(topic);
        return ResponseEntity.ok("Unsubscribed from topic: " + topic);
    }

    /**
     *
     * @param topic
     * @param authHeader request with header {"Autorization": 'Bearer ${token}'}
     * @return recent 10 message from this topic
     */
    @GetMapping("/recent")
    public ResponseEntity<?> getRecent(@RequestParam String topic, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String username = tokenService.extractUsername(token);
        if (!accessControlService.userHasAccessToTopic(username, topic)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You do not have access to this topic");
        }
        return ResponseEntity.ok(mqttMessageHandler.getRecentMessages(topic));
    }
}
