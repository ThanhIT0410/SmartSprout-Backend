package org.smartsproutbackend.exception;

import java.time.LocalDateTime;

public class DeviceAlreadyExecutingException extends RuntimeException {
    private final LocalDateTime endTime;

    public DeviceAlreadyExecutingException(LocalDateTime endTime) {
        super("Device is currently watering");
        this.endTime = endTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }
}
