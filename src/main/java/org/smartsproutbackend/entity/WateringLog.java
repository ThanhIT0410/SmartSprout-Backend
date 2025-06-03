package org.smartsproutbackend.entity;

import jakarta.persistence.*;
import org.smartsproutbackend.enums.WateringOperation;

import java.time.LocalDateTime;

@Entity
public class WateringLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int logId;

    private String deviceId;
    private String deviceName;

    @Enumerated(EnumType.STRING)
    private WateringOperation operation;
    private LocalDateTime executeTime;
    private int duration;

    public int getLogId() {
        return logId;
    }

    public void setLogId(int logId) {
        this.logId = logId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public WateringOperation getOperation() {
        return operation;
    }

    public void setOperation(WateringOperation operation) {
        this.operation = operation;
    }

    public LocalDateTime getExecuteTime() {
        return executeTime;
    }

    public void setExecuteTime(LocalDateTime executeTime) {
        this.executeTime = executeTime;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
