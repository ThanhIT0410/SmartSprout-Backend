package org.smartsproutbackend.repository;

import org.smartsproutbackend.entity.DevicePair;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DevicePairRepository extends JpaRepository<DevicePair, Long> {
    List<DevicePair> findByUserId(Long userId);
    Optional<DevicePair> findByTopic(String topic);
}