package org.smartsproutbackend.service;

import org.smartsproutbackend.entity.DevicePair;
import org.smartsproutbackend.entity.User;
import org.smartsproutbackend.repository.DevicePairRepository;
import org.smartsproutbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserLoginService {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DevicePairRepository devicePairRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Optional<String> authenticate(String username, String password) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                String token = tokenService.generateToken(user);
                return Optional.of(token);
            }
        }
        return Optional.empty();
    }

    public List<DevicePair> findDevicePairs(String username) {
        return userRepository.findByUsername(username)
                .map(user -> devicePairRepository.findByUserId(user.getUserId()))
                .orElse(Collections.emptyList())
                .stream()
                .toList();
    }

    public boolean deleteDevice(String username, String topic) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            return false;
        }

        Optional<DevicePair> devicePairOptional = devicePairRepository.findByTopic(topic);
        if (devicePairOptional.isEmpty()) {
            return false;
        }

        DevicePair devicePair = devicePairOptional.get();
        if (!devicePair.getUserId().equals(userOptional.get().getUserId())) {
            return false;
        }

        devicePairRepository.delete(devicePair);
        return true;
    }
}
