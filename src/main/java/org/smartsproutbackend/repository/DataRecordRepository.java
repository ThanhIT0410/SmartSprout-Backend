package org.smartsproutbackend.repository;

import org.smartsproutbackend.entity.DataRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DataRecordRepository extends JpaRepository<DataRecord, Long> {
    List<DataRecord> findTop10ByDeviceIdOrderByTimestampDesc(Long deviceId);
}
