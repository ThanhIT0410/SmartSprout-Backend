package org.smartsproutbackend.service;

import org.smartsproutbackend.dto.WateringPlanRequest;
import org.smartsproutbackend.entity.WateringPlan;
import org.smartsproutbackend.exception.PlanNotFoundException;
import org.smartsproutbackend.repository.WateringPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class PlanSavingService {

    @Autowired
    private WateringPlanRepository wateringPlanRepository;

    public WateringPlan savePlan(WateringPlanRequest wateringPlanRequest) {
        WateringPlan plan = new WateringPlan();
        plan.setDeviceId(wateringPlanRequest.getDeviceId());
        plan.setDeviceName(wateringPlanRequest.getDeviceName());
        plan.setTime(wateringPlanRequest.getTime());
        plan.setRepeatType(wateringPlanRequest.getRepeatType());
        plan.setIntervalDays(wateringPlanRequest.getIntervalDays());
        plan.setWeekDays(wateringPlanRequest.getWeekDays());
        plan.setDuration(wateringPlanRequest.getDuration());
        plan.setStartDate(LocalDate.now());
        plan.setActive(wateringPlanRequest.isActive());
        plan.setLastExecutedDate(null);

        return wateringPlanRepository.save(plan);
    }

    public WateringPlan updatePlan(Long planId, WateringPlanRequest wateringPlanRequest) throws PlanNotFoundException {
        WateringPlan plan = wateringPlanRepository.findById(planId)
                .orElseThrow(() -> new PlanNotFoundException("Plan not found with ID: " + planId));
        plan.setDeviceId(wateringPlanRequest.getDeviceId());
        plan.setDeviceName(wateringPlanRequest.getDeviceName());
        plan.setTime(wateringPlanRequest.getTime());
        plan.setRepeatType(wateringPlanRequest.getRepeatType());
        plan.setIntervalDays(wateringPlanRequest.getIntervalDays());
        plan.setWeekDays(wateringPlanRequest.getWeekDays());
        plan.setDuration(wateringPlanRequest.getDuration());
        plan.setActive(wateringPlanRequest.isActive());

        return wateringPlanRepository.save(plan);
    }

    public List<WateringPlan> getAllPlans(String deviceId) {
        return wateringPlanRepository.findByDeviceId(deviceId);
    }

    public void deletePlan(Long planId) throws PlanNotFoundException {
        WateringPlan plan = wateringPlanRepository.findById(planId)
                .orElseThrow(() -> new PlanNotFoundException("Plan not found with ID: " + planId));
        wateringPlanRepository.delete(plan);
    }
}
