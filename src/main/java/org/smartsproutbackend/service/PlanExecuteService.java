package org.smartsproutbackend.service;

import org.smartsproutbackend.entity.WateringPlan;
import org.smartsproutbackend.repository.WateringPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PlanExecuteService {

    @Autowired
    private WateringPlanRepository wateringPlanRepository;

    @Autowired
    private WateringService wateringService;

    @Scheduled(fixedRate = 60_000)
    public void checkAndTriggerWateringPlan() {
        LocalTime now = LocalTime.now();
        LocalDate today = LocalDate.now();
        DayOfWeek dayOfWeek = today.getDayOfWeek();

        System.out.println("It is currently " + today + " and " + now);

        List<WateringPlan> plans = wateringPlanRepository.findAll();

        List<WateringPlan> runnablePlans = plans.stream()
                .filter(WateringPlan::isActive)
                .filter(p -> !now.isBefore(p.getTime()) && now.isBefore(p.getTime().plusMinutes(1)))
                .filter(p -> runToday(p, today, dayOfWeek))
                .filter(p -> p.getLastExecutedDate() == null || !p.getLastExecutedDate().equals(today))
                .toList();

        Map<String, List<WateringPlan>> plansByDevice = runnablePlans.stream()
                .collect(Collectors.groupingBy(WateringPlan::getDeviceId));

        System.out.println("Running plans: " + plansByDevice);

        for (Map.Entry<String, List<WateringPlan>> entry : plansByDevice.entrySet()) {
            String deviceId = entry.getKey();
            List<WateringPlan> devicePlans = entry.getValue();

            WateringPlan representativePlan = devicePlans.getFirst();

            wateringService.startWatering(
                    representativePlan.getDeviceId(),
                    representativePlan.getDeviceName(),
                    representativePlan.getDuration()
            );

            for (WateringPlan plan : devicePlans) {
                plan.setLastExecutedDate(today);
            }
            wateringPlanRepository.saveAll(devicePlans);
        }
    }

    private boolean runToday(WateringPlan plan, LocalDate today, DayOfWeek dayOfWeek) {
        if (plan.getStartDate() != null && today.isBefore(plan.getStartDate())) return false;

        return switch (plan.getRepeatType()) {
            case DAILY -> true;

            case EVERY_X_DAYS -> {
                if (plan.getStartDate() == null || plan.getIntervalDays() == null) yield false;
                long daysDiff = ChronoUnit.DAYS.between(plan.getStartDate(), today);
                yield daysDiff % plan.getIntervalDays() == 0;
            }

            case WEEKDAYS -> {
                Set<DayOfWeek> validDays = plan.getWeekDays();
                yield validDays != null && validDays.contains(dayOfWeek);
            }
        };
    }
}
