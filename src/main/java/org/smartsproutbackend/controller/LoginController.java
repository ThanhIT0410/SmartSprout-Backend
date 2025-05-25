package org.smartsproutbackend.controller;

import jakarta.validation.Valid;
import org.smartsproutbackend.dto.LoginRequest;
import org.smartsproutbackend.service.TokenService;
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

    @Autowired
    private TokenService tokenService;

    /**
     *
     * @param loginRequest (username, password)
     * @return token
     */
    @PostMapping("/auth")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        return userLoginService
                .authenticate(loginRequest.getUsername(), loginRequest.getPassword())
                .map(token -> ResponseEntity.ok(Map.of("token", token)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid username or password")));
    }

    /**
     *
     * @param authHeader request with header {"Autorization": 'Bearer ${token}'}
     * @return map of (deviceName, topic)
     */
    @GetMapping("/devices")
    public ResponseEntity<Map<String, String>> getDevices(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String username = tokenService.extractUsername(token);
        return ResponseEntity.ok(userLoginService.findDevicePairs(username));
    }
}
