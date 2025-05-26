package org.smartsproutbackend.controller;

import jakarta.validation.Valid;
import org.smartsproutbackend.dto.AddDeviceRequest;
import org.smartsproutbackend.dto.RegisterRequest;
import org.smartsproutbackend.service.UserRegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/register")
public class RegisterController {

    @Autowired
    private UserRegisterService userRegisterService;

    @Value("${esp.secret}")
    private String espSecret;

    /**
     *
     * @param secret esp8266 secret key in application.properties
     * @param registerRequest (String email, String password)
     * @return ok
     */
    @PostMapping("/new-user")
    public ResponseEntity<?> registerUser(@RequestHeader("Esp8266") String secret, @Valid @RequestBody RegisterRequest registerRequest) {
        if (!secret.equals(espSecret)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        boolean success = userRegisterService.register(registerRequest.getUsername(), registerRequest.getPassword());
        if (!success) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists");
        }
        return ResponseEntity.ok("Registered Successfully");
    }

    /**
     *
     * @param secret esp8266 secret key in application.properties
     * @param addDeviceRequest (String email, String deviceName, String deviceId
     * @return String topic
     */
    @PostMapping("/new-device")
    public ResponseEntity<?> registerDevice(@RequestHeader("Esp8266") String secret, @Valid @RequestBody AddDeviceRequest addDeviceRequest) {
        if (!secret.equals(espSecret)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        String topic = userRegisterService.addDevice(addDeviceRequest.getUsername(), addDeviceRequest.getDeviceName(), addDeviceRequest.getDeviceId());
        if (topic == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User not found or device already exists");
        }
        return ResponseEntity.ok(Map.of("topic", topic));
    }
}
