package org.smartsproutbackend.service;

import org.smartsproutbackend.entity.WateringLog;
import org.smartsproutbackend.enums.WateringOperation;
import org.smartsproutbackend.repository.WateringLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecentLogServices {

    @Autowired
    private WateringLogRepository wateringLogRepository;

    public List<WateringLog> getRecentLogs(String deviceId) {
        return wateringLogRepository.findTop10ByDeviceIdAndOperationOrderByExecuteTimeDesc(deviceId, WateringOperation.START);
    }
}
