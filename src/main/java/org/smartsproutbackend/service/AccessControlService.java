package org.smartsproutbackend.service;

import org.smartsproutbackend.entity.DevicePair;
import org.smartsproutbackend.entity.User;
import org.smartsproutbackend.repository.DevicePairRepository;
import org.smartsproutbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccessControlService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DevicePairRepository devicePairRepository;

    public boolean userHasAccessToTopic(String username, String topic) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            List<DevicePair> devicePairs= devicePairRepository.findByUserId(user.get().getUserId());
            return devicePairs.stream().anyMatch(devicePair -> devicePair.getTopic().equals(topic));
        }
        return false;
    }
}
