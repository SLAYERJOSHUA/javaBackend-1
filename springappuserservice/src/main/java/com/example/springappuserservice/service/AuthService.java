package com.example.springappuserservice.service;

import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.springappuserservice.model.User;
import com.example.springappuserservice.repository.UserRepository;

import io.jsonwebtoken.Claims;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            UserService userService,
            JwtService jwtService) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    public User register(User user) {
        return userService.createUser(user);
    }

    public Optional<Map<String, Object>> login(String username, String password, String deviceInfo, String ipAddress) {
        return userRepository.findByUsername(username)
                .filter(user -> Boolean.TRUE.equals(user.getIsActive()))
                .filter(user -> user.getPassword().equals(userService.hashPassword(password)))
                .map(this::createTokenResponse);
    }

    public Optional<Map<String, Object>> refresh(String token) {
        return jwtService.getUserId(token)
                .flatMap(userRepository::findById)
                .filter(user -> Boolean.TRUE.equals(user.getIsActive()))
                .map(this::createTokenResponse);
    }

    public Optional<Map<String, Object>> validate(String token) {
        return jwtService.validateToken(token)
                .flatMap(this::findActiveUser)
                .map(user -> Map.of("valid", true, "user", userResponse(user)));
    }

    public boolean changePassword(String token, String oldPassword, String newPassword) {
        return jwtService.getUserId(token)
                .flatMap(userRepository::findById)
                .filter(user -> user.getPassword().equals(userService.hashPassword(oldPassword)))
                .map(user -> {
                    user.setPassword(userService.hashPassword(newPassword));
                    userRepository.save(user);
                    return true;
                })
                .orElse(false);
    }

    public Map<String, Object> getActiveSessions() {
        return Map.of("sessions", java.util.List.of(), "count", 0, "message", "JWT authentication is stateless");
    }

    public boolean terminateSession(String sessionId) {
        return false;
    }

    public boolean validateSession(String sessionId) {
        return false;
    }

    private Map<String, Object> createTokenResponse(User user) {
        return Map.of("token", jwtService.generateToken(user), "type", "Bearer", "user", userResponse(user));
    }

    private Optional<User> findActiveUser(Claims claims) {
        return Optional.ofNullable(claims.getSubject())
                .map(Long::valueOf)
                .flatMap(userRepository::findById)
                .filter(user -> Boolean.TRUE.equals(user.getIsActive()));
    }

    private Map<String, Object> userResponse(User user) {
        return Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "email", user.getEmail(),
                "firstName", user.getFirstName(),
                "lastName", user.getLastName(),
                "role", user.getRole(),
                "isActive", user.getIsActive());
    }
}
