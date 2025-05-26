package org.smartsproutbackend.controller;

import org.smartsproutbackend.dto.WateringPlanRequest;
import org.smartsproutbackend.dto.WateringTriggerRequest;
import org.smartsproutbackend.entity.WateringPlan;
import org.smartsproutbackend.service.PlanSavingService;
import org.smartsproutbackend.service.WateringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/watering")
public class WateringController {

    @Autowired
    private WateringService wateringService;

    @Autowired
    private PlanSavingService planSavingService;

    @PostMapping("/immediate")
    public ResponseEntity<String> immediatelyTriggerWatering(@RequestBody WateringTriggerRequest request) {
        wateringService.triggerWatering(request.getDeviceId(), request.getDeviceName(), request.getDuration());
        return ResponseEntity.ok("Watering successfully");
    }

    @PostMapping("/new-plan")
    public ResponseEntity<WateringPlan> scheduleWatering(@RequestBody WateringPlanRequest request) {
        WateringPlan plan = planSavingService.savePlan(request);
        return ResponseEntity.ok(plan);
    }

    @PutMapping("/change-plan/{planId}")
    public ResponseEntity<WateringPlan> changeSchedule(@PathVariable Long planId, @RequestBody WateringPlanRequest request) {
        WateringPlan plan = planSavingService.updatePlan(planId, request);
        return ResponseEntity.ok(plan);
    }

    @GetMapping("/all-plan")
    public ResponseEntity<List<WateringPlan>> getAllPlans(@RequestParam String deviceId) {
        List<WateringPlan> allPlans = planSavingService.getAllPlans(deviceId);
        return ResponseEntity.ok(allPlans);
    }
}
