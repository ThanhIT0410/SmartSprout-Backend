package org.smartsproutbackend.controller;

import org.smartsproutbackend.dto.WateringPlanRequest;
import org.smartsproutbackend.dto.WateringTriggerRequest;
import org.smartsproutbackend.entity.WateringPlan;
import org.smartsproutbackend.service.AccessControlService;
import org.smartsproutbackend.service.PlanSavingService;
import org.smartsproutbackend.service.TokenService;
import org.smartsproutbackend.service.WateringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AccessControlService accessControlService;

    /**
     *
     * @param authHeader request with header {"Authorization": 'Bearer ${token}'}
     * @param request (String deviceId, String deviceName, int duration)
     * @return watering
     */
    @PostMapping("/immediate")
    public ResponseEntity<String> immediatelyTriggerWatering(@RequestHeader("Authorization") String authHeader,
                                                             @RequestBody WateringTriggerRequest request) {
        String token = authHeader.replace("Bearer ", "");
        String username = tokenService.extractUsername(token);
        if (!accessControlService.userHasAccessToDevice(username, request.getDeviceId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You do not have access to this device");
        }

        try {
            wateringService.triggerWatering(
                    request.getDeviceId(),
                    request.getDeviceName(),
                    request.getDuration()
            );
            return ResponseEntity.ok("Watering successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    /**
     *
     * @param authHeader request with header {"Authorization": 'Bearer ${token}'}
     * @param request (String deviceId, String deviceName, LocalTime time, RepeatType repeatType.
     *                  private Integer intervalDays, private Set<DayOfWeek> weekDays, int duration;
     *                  private boolean active;
     * @return new plan
     */
    @PostMapping("/new-plan")
    public ResponseEntity<?> scheduleWatering(@RequestHeader("Authorization") String authHeader,
                                              @RequestBody WateringPlanRequest request) {
        String token = authHeader.replace("Bearer ", "");
        String username = tokenService.extractUsername(token);
        if (!accessControlService.userHasAccessToDevice(username, request.getDeviceId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You do not have access to this device");
        }

        WateringPlan plan = planSavingService.savePlan(request);
        return ResponseEntity.ok(plan);

    }

    /**
     *
     * @param authHeader request with header {"Authorization": 'Bearer ${token}'}
     * @param planId
     * @param request (String deviceId, String deviceName, LocalTime time, RepeatType repeatType.
     *      *                  private Integer intervalDays, private Set<DayOfWeek> weekDays, int duration;
     *      *                  private boolean active;
     * @return updated plan
     */
    @PutMapping("/change-plan/{planId}")
    public ResponseEntity<?> changeSchedule(@RequestHeader("Authorization") String authHeader,
                                                       @PathVariable Long planId,
                                                       @RequestBody WateringPlanRequest request) {
        String token = authHeader.replace("Bearer ", "");
        String username = tokenService.extractUsername(token);
        if (!accessControlService.userHasAccessToDevice(username, request.getDeviceId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You do not have access to this device");
        }

        WateringPlan plan = planSavingService.updatePlan(planId, request);
        return ResponseEntity.ok(plan);
    }

    /**
     *
     * @param authHeader request with header {"Authorization": 'Bearer ${token}'}
     * @param deviceId
     * @return list of plans
     */
    @GetMapping("/all-plan")
    public ResponseEntity<?> getAllPlans(@RequestHeader("Authorization") String authHeader,
                                                          @RequestParam String deviceId) {
        String token = authHeader.replace("Bearer ", "");
        String username = tokenService.extractUsername(token);
        if (!accessControlService.userHasAccessToDevice(username, deviceId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You do not have access to this device");
        }

        List<WateringPlan> allPlans = planSavingService.getAllPlans(deviceId);
        return ResponseEntity.ok(allPlans);
    }
}
