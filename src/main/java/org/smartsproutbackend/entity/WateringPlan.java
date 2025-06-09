package org.smartsproutbackend.entity;

import jakarta.persistence.*;
import org.smartsproutbackend.enums.RepeatType;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

@Entity
public class WateringPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long planId;

    private LocalTime time;

    @Enumerated(EnumType.STRING)
    private RepeatType repeatType;

    private Integer intervalDays; // Nếu repeatType = EVERY_X_DAYS

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "watering_plan_week_days", joinColumns = @JoinColumn(name = "plan_id"))
    @Column(name = "week_day")
    @Enumerated(EnumType.STRING)
    private Set<DayOfWeek> weekDays;

    private String deviceId;
    private String deviceName;

    private int duration;
    private boolean active;
    private LocalDate startDate;
    private LocalDate lastExecutedDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WateringPlan)) return false;
        return this.getPlanId() != null && this.getPlanId().equals(((WateringPlan) o).getPlanId());
    }

    @Override
    public int hashCode() {
        return getPlanId() != null ? getPlanId().hashCode() : 0;
    }

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public RepeatType getRepeatType() {
        return repeatType;
    }

    public void setRepeatType(RepeatType repeatType) {
        this.repeatType = repeatType;
    }

    public Integer getIntervalDays() {
        return intervalDays;
    }

    public void setIntervalDays(Integer intervalDays) {
        this.intervalDays = intervalDays;
    }

    public Set<DayOfWeek> getWeekDays() {
        return weekDays;
    }

    public void setWeekDays(Set<DayOfWeek> weekDays) {
        this.weekDays = weekDays;
    }

    public int getDuration() {
        return duration;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getLastExecutedDate() {
        return lastExecutedDate;
    }

    public void setLastExecutedDate(LocalDate lastExecutedDate) {
        this.lastExecutedDate = lastExecutedDate;
    }
}
