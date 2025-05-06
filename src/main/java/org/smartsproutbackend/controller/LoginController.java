package org.smartsproutbackend.controller;

import org.smartsproutbackend.dto.DeviceRequestByUsername;
import org.smartsproutbackend.dto.LoginRequest;
import org.smartsproutbackend.service.UserLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/login")
public class LoginController {

    @Autowired
    private UserLoginService userLoginService;

    @PostMapping("/auth")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        boolean isAuthenticated = userLoginService.authenticate(loginRequest.getUsername(), loginRequest.getPassword());
        if (isAuthenticated) {
            return ResponseEntity.ok("Login successful");
        }
        else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }

    @PostMapping("/devices")
    public Map<String, String> getDevices(@RequestBody DeviceRequestByUsername deviceRequestByUsername) {
        return userLoginService.findDevicePairs(deviceRequestByUsername.getUsername());
    }
}
