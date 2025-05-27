package org.smartsproutbackend.controller;

import jakarta.validation.Valid;
import org.smartsproutbackend.dto.LoginRequest;
import org.smartsproutbackend.entity.DevicePair;
import org.smartsproutbackend.service.TokenService;
import org.smartsproutbackend.service.UserLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
     * @param loginRequest (String email, String password)
     * @return String token
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
     * @param authHeader request with header {"Authorization": 'Bearer ${token}'}
     * @return map of DevicePair
     */
    @GetMapping("/devices")
    public ResponseEntity<List<DevicePair>> getDevices(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String username = tokenService.extractUsername(token);
        return ResponseEntity.ok(userLoginService.findDevicePairs(username));
    }

    /**
     *
     * @param authHeader request with header {"Authorization": 'Bearer ${token}'}
     * @param topic topic of the device to delete
     * @return success or error message
     */
    @DeleteMapping("/devices")
    public ResponseEntity<?> deleteDevice(@RequestHeader("Authorization") String authHeader, @RequestParam String topic) {
        String token = authHeader.replace("Bearer ", "");
        String username = tokenService.extractUsername(token);

        boolean success = userLoginService.deleteDevice(username, topic);
        if (success) {
            return ResponseEntity.ok(Map.of("message", "Device deleted successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Device not found or unauthorized"));
        }
    }
}
