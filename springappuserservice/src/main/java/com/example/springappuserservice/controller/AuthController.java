package com.example.springappuserservice.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.springappuserservice.model.User;
import com.example.springappuserservice.service.AuthService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@Valid @RequestBody User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(user));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request.username(), request.password(), request.deviceInfo(), request.ipAddress())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, Object>> refresh(@Valid @RequestBody TokenRequest request) {
        return authService.refresh(request.token())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateToken(@Valid @RequestBody TokenRequest request) {
        return authService.validate(request.token())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("valid", false)));
    }

    @PostMapping("/change-password")
    public ResponseEntity<Map<String, String>> changePassword(
            @RequestHeader(name = "Authorization", required = false) String authorization,
            @Valid @RequestBody ChangePasswordRequest request) {
        String token = authorization != null && authorization.startsWith("Bearer ")
                ? authorization.substring(7)
                : request.token();
        if (authService.changePassword(token, request.oldPassword(), request.newPassword())) {
            return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid token or password"));
    }

    public record LoginRequest(
            @NotBlank(message = "username is required") String username,
            @NotBlank(message = "password is required") String password,
            String deviceInfo,
            String ipAddress) {
    }

    public record TokenRequest(@NotBlank(message = "token is required") String token) {
    }

    public record ChangePasswordRequest(
            String token,
            @NotBlank(message = "oldPassword is required") String oldPassword,
            @NotBlank(message = "newPassword is required")
            @Size(min = 6, message = "newPassword must be at least 6 characters") String newPassword) {
    }
}
