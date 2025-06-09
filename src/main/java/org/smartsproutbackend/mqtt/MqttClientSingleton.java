package org.smartsproutbackend.mqtt;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.eclipse.paho.client.mqttv3.*;
import org.smartsproutbackend.entity.DevicePair;
import org.smartsproutbackend.repository.DevicePairRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

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

    @Autowired
    private DevicePairRepository devicePairRepository;

    private final MqttMessageHandler mqttMessageHandler;

    public MqttClientSingleton(MqttMessageHandler mqttMessageHandler) {
        this.mqttMessageHandler = mqttMessageHandler;
    }

    @PostConstruct
    public void init() throws MqttException {
        try {
            mqttClient = new MqttClient(brokerUrl, clientId);
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            options.setUserName(username);
            options.setPassword(password.toCharArray());

            mqttClient.connect(options);
            mqttClient.setCallback(mqttMessageHandler);

            List<String> topics = devicePairRepository.findAll()
                    .stream()
                    .map(DevicePair::getTopic)
                    .toList();

            for (String topic : topics) {
                mqttClient.subscribe(topic, 0);
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void subscribeToTopic(String topic) throws MqttException {
        if (!mqttClient.isConnected()) init();
        mqttClient.subscribe(topic);
    }

    public void unsubscribeToTopic(String topic) throws MqttException {
        if (mqttClient.isConnected()) {
            mqttClient.unsubscribe(topic);
        }
    }

    public void publishToTopic(String topic, String payload) throws MqttException {
        if (!mqttClient.isConnected()) init();
        MqttMessage message = new MqttMessage(payload.getBytes());
        message.setQos(1);
        mqttClient.publish(topic, message);
    }

    public boolean isConnected() {
        return mqttClient != null && mqttClient.isConnected();
    }

    private void ensureConnected() throws MqttException {
        if (mqttClient == null || !mqttClient.isConnected()) {
            init();
        }
    }

    @PreDestroy
    public void cleanup() throws MqttException {
        if (mqttClient != null && mqttClient.isConnected()) {
            mqttClient.disconnect();
            mqttClient.close();
        }
    }
}
