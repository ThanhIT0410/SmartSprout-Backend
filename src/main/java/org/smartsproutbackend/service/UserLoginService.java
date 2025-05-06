package org.smartsproutbackend.service;

import org.smartsproutbackend.entity.DevicePair;
import org.smartsproutbackend.entity.User;
import org.smartsproutbackend.repository.DevicePairRepository;
import org.smartsproutbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserLoginService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DevicePairRepository devicePairRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public boolean authenticate(String username, String password) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return passwordEncoder.matches(password, user.getPassword());
        }
        return false;
    }

    public Map<String, String> findDevicePairs(String username) {
        return userRepository.findByUsername(username)
                .map(user -> devicePairRepository.findByUserId(user.getUserId()))
                .orElse(Collections.emptyList())
                .stream()
                .collect(Collectors.toMap(
                        DevicePair::getDeviceName,
                        DevicePair::getTopic
                ));
    }
}
