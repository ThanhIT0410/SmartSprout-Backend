package org.smartsproutbackend.repository;

import org.smartsproutbackend.entity.WateringLog;
import org.smartsproutbackend.enums.WateringOperation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WateringLogRepository extends JpaRepository<WateringLog, Long> {
    List<WateringLog> findTop10ByDeviceIdAndOperationOrderByExecuteTimeDesc(String deviceId, WateringOperation operation);
    Optional<WateringLog> findTopByDeviceIdOrderByExecuteTimeDesc(String deviceId);
}
