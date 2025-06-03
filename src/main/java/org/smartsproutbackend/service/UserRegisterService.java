package org.smartsproutbackend.service;

import org.smartsproutbackend.entity.DevicePair;
import org.smartsproutbackend.entity.User;
import org.smartsproutbackend.repository.DevicePairRepository;
import org.smartsproutbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserRegisterService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DevicePairRepository devicePairRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public boolean register(String username, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            return false;
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        return true;
    }

    public String addDevice(String username, String deviceName, String deviceId) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) return null;

        DevicePair pair = new DevicePair();
        pair.setUserId(user.get().getUserId());
        pair.setDeviceName(deviceName);
        pair.setDeviceId(deviceId);

        String topic = "sensor/" + pair.getDeviceId();
        pair.setTopic(topic);
        devicePairRepository.save(pair);
        return topic;
    }
}
