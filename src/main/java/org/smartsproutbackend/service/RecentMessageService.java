package org.smartsproutbackend.service;

import org.smartsproutbackend.repository.RecentMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RecentMessageService {

    @Autowired
    private RecentMessageRepository recentMessageRepository;

    public List<Map<String, Object>> getRecentMessages(String topic) {
        return recentMessageRepository.findTop10ByTopicOrderByTimestampDesc(topic)
                .stream()
                .map(msg -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("air", msg.getAir());
                    result.put("temp", msg.getTemp());
                    result.put("soil", msg.getSoil());
                    result.put("timestamp", msg.getTimestamp().toString());
                    return result;
                })
                .collect(Collectors.toList());
    }
}

