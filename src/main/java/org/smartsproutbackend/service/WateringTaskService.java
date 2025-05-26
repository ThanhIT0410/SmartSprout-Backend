package org.smartsproutbackend.service;

import org.smartsproutbackend.repository.WateringLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WateringTaskService {

    @Autowired
    private WateringLogRepository wateringLogRepository;

}
