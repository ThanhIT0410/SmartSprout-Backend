package org.smartsproutbackend.service;

import org.smartsproutbackend.entity.WateringPlan;
import org.smartsproutbackend.exception.DeviceAlreadyExecutingException;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class PlanExecuteService {

    @Autowired
    private WateringPlanRepository wateringPlanRepository;

    @Autowired
    private WateringService wateringService;

    private final Map<WateringPlan, Integer> retryCount = new ConcurrentHashMap<>();

    @Scheduled(fixedRate = 30_000)
    public void checkAndTriggerWateringPlan() {
        LocalTime now = LocalTime.now();
        LocalDate today = LocalDate.now();
        DayOfWeek dayOfWeek = today.getDayOfWeek();

        System.out.println("It is currently " + today + " and " + now);

        // Retry
        Set<WateringPlan> retryPlans = retryCount.keySet();
        for (WateringPlan plan : retryPlans) {
            try {
                wateringService.startWatering(
                        plan.getDeviceId(),
                        plan.getDeviceName(),
                        plan.getDuration()
                );
                plan.setLastExecutedDate(today);
                wateringPlanRepository.save(plan);
                retryCount.remove(plan);
            } catch (DeviceAlreadyExecutingException e) {
                int retry = retryCount.get(plan);
                if (retry < 10) retryCount.put(plan, retry + 1);
                else retryCount.remove(plan);
            }
        }

        List<WateringPlan> plans = wateringPlanRepository.findAll();

        List<WateringPlan> runnablePlans = plans.stream()
                .filter(WateringPlan::isActive)
                .filter(p -> !now.isBefore(p.getTime()) && now.isBefore(p.getTime().plusSeconds(30)))
                .filter(p -> runToday(p, today, dayOfWeek))
                .filter(p -> p.getLastExecutedDate() == null || !p.getLastExecutedDate().equals(today))
                .toList();

        Map<String, List<WateringPlan>> plansByDevice = runnablePlans.stream()
                .collect(Collectors.groupingBy(WateringPlan::getDeviceId));

        System.out.println("Running plans: " + plansByDevice);

        for (WateringPlan plan : runnablePlans) {
            try {
                wateringService.startWatering(
                        plan.getDeviceId(),
                        plan.getDeviceName(),
                        plan.getDuration()
                );

                plan.setLastExecutedDate(today);
                wateringPlanRepository.save(plan);

            } catch (DeviceAlreadyExecutingException e) {
                retryCount.put(plan, 1);
            }
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
