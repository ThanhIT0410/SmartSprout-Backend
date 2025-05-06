package org.smartsproutbackend.mqtt;

import jakarta.annotation.PostConstruct;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MqttClientSingleton {
    private static final String BROKER_URL = "tcp://localhost:1883";
    private static final String CLIENT_ID = "SmartSprout Backend Server";
    private static MqttClient mqttClient;

    @Autowired
    private MqttMessageHandler mqttMessageHandler;

    @PostConstruct
    public void init() throws MqttException {
        mqttClient = new MqttClient(BROKER_URL, CLIENT_ID);
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);
        options.setAutomaticReconnect(true);
        mqttClient.setCallback(mqttMessageHandler);
        mqttClient.connect(options);
        mqttClient.subscribe("iot/+/data");
    }

    public void subscribeToTopic(String topic) throws MqttException {
        mqttClient.subscribe(topic);
    }
}
