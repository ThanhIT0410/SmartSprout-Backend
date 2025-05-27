package org.smartsproutbackend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class RecentMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long msgId;

    private String topic;
    private float air;
    private float temp;
    private float soil;
    private LocalDateTime timestamp;

    public Long getMsgId() {
        return msgId;
    }

    public void setMsgId(Long msgId) {
        this.msgId = msgId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public float getAir() {
        return air;
    }

    public void setAir(float air) {
        this.air = air;
    }

    public float getTemp() {
        return temp;
    }

    public void setTemp(float temp) {
        this.temp = temp;
    }

    public float getSoil() {
        return soil;
    }

    public void setSoil(float soil) {
        this.soil = soil;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}