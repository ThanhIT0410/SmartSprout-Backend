package org.smartsproutbackend.repository;

import org.smartsproutbackend.entity.WateringPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WateringPlanRepository extends JpaRepository<WateringPlan, Long> {
    List<WateringPlan> findByDeviceId(String deviceId);
}
