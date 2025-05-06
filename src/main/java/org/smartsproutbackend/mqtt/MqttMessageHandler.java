package org.smartsproutbackend.mqtt;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.smartsproutbackend.entity.DataRecord;
import org.smartsproutbackend.repository.DataRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Component
public class MqttMessageHandler implements MqttCallback {

    @Autowired
    private DataRecordRepository dataRecordRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private final Gson gson = new Gson();

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String payload = new String(message.getPayload(), StandardCharsets.UTF_8);

        JsonObject jsonObject = gson.fromJson(payload, JsonObject.class);
        double temperature = jsonObject.get("temperature").getAsDouble();
        double airHumidity = jsonObject.get("airHumidity").getAsDouble();
        double soilHumidity = jsonObject.get("soilHumidity").getAsDouble();
        LocalDateTime currentDateTime = LocalDateTime.now();

        DataRecord dataRecord = new DataRecord();
        dataRecord.setTemperature(temperature);
        dataRecord.setAirHumidity(airHumidity);
        dataRecord.setSoilHumidity(soilHumidity);
        dataRecord.setTimestamp(currentDateTime);
        dataRecordRepository.save(dataRecord);
        messagingTemplate.convertAndSend("/topic/"+topic, gson.toJson(dataRecord));
    }

    @Override
    public void connectionLost(Throwable cause) {}

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {}
}
