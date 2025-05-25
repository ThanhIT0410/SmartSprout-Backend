package org.smartsproutbackend.repository;

import org.smartsproutbackend.entity.DevicePair;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DevicePairRepository extends JpaRepository<DevicePair, Long> {
    List<DevicePair> findByUserId(Long userId);
}
