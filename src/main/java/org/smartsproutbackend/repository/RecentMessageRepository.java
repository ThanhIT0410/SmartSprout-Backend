package org.smartsproutbackend.repository;

import org.smartsproutbackend.entity.RecentMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface RecentMessageRepository extends JpaRepository<RecentMessage, Long> {
    List<RecentMessage> findByTopicOrderByTimestampDesc(String topic);

    @Modifying
    @Transactional
    void deleteByTopicAndTimestampBefore(String topic, LocalDateTime cutoff);

    List<RecentMessage> findByTopicAndTimestampBefore(String topic, LocalDateTime cutoff);
}
