package org.smartsproutbackend.repository;

import org.smartsproutbackend.entity.WateringLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WateringLogRepository extends JpaRepository<WateringLog, Long> {
}
