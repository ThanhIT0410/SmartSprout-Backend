package org.smartsproutbackend.mqtt;

import jakarta.annotation.PostConstruct;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MqttClientSingleton {

    private IMqttClient mqttClient;

    @Value("${mqtt.broker}")
    private String brokerUrl;

    @Value("${mqtt.client-id}")
    private String clientId;

    @Value("${mqtt.username}")
    private String username;

    @Value("${mqtt.password}")
    private String password;

    private final MqttMessageHandler mqttMessageHandler;

    public MqttClientSingleton(MqttMessageHandler mqttMessageHandler) {
        this.mqttMessageHandler = mqttMessageHandler;
    }

    @PostConstruct
    public void init() throws MqttException {
        mqttClient = new MqttClient(brokerUrl, clientId);
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);
        options.setUserName(username);
        options.setPassword(password.toCharArray());

        mqttClient.connect(options);
        mqttClient.setCallback(mqttMessageHandler);
    }

    public void subscribeToTopic(String topic) throws MqttException {
        if (!mqttClient.isConnected()) init();
        mqttClient.subscribe(topic);
    }

    public boolean isConnected() {
        return mqttClient != null && mqttClient.isConnected();
    }
}
