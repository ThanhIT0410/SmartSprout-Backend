package org.smartsproutbackend.controller;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.smartsproutbackend.mqtt.MqttClientSingleton;
import org.smartsproutbackend.mqtt.MqttMessageHandler;
import org.smartsproutbackend.service.AccessControlService;
import org.smartsproutbackend.service.RecentMessageService;
import org.smartsproutbackend.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/data-streaming")
public class DataStreamController {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AccessControlService accessControlService;

    @Autowired
    private RecentMessageService recentMessageService;

    @GetMapping("/recent-data")
    public ResponseEntity<?> getRecentData(@RequestParam String topic, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String username = tokenService.extractUsername(token);

        if (!accessControlService.userHasAccessToTopic(username, topic)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You do not have access to this topic");
        }

        return ResponseEntity.ok(recentMessageService.getRecentMessages(topic));
    }
}
