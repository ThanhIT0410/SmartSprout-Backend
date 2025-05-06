package org.smartsproutbackend.controller;

import org.smartsproutbackend.dto.DeviceRequestByTopic;
import org.smartsproutbackend.entity.DataRecord;
import org.smartsproutbackend.entity.DevicePair;
import org.smartsproutbackend.repository.DataRecordRepository;
import org.smartsproutbackend.repository.DevicePairRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/data-streaming")
public class DataStreamController {

    @Autowired
    private DevicePairRepository devicePairRepository;

    @Autowired
    private DataRecordRepository dataRecordRepository;

    @PostMapping("/start")
    public List<DataRecord> startDataStream(@RequestBody DeviceRequestByTopic deviceRequestByTopic) {
        String topic = deviceRequestByTopic.getTopic();

        Optional<DevicePair> devicePairOptional = devicePairRepository.findByTopic(topic);
        if (devicePairOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Topic not found");
        }
        Long deviceId = devicePairOptional.get().getDeviceId();
        return dataRecordRepository.findTop10ByDeviceIdOrderByTimestampDesc(deviceId);
    }
}
