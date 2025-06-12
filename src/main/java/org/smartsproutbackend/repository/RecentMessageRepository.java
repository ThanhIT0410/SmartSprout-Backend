package org.smartsproutbackend.repository;

import org.smartsproutbackend.entity.RecentMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RecentMessageRepository extends JpaRepository<RecentMessage, Long> {
    List<RecentMessage> findTop10ByTopicOrderByTimestampDesc(String topic);
}
