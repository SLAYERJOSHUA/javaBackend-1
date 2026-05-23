package com.example.springappuserservice.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.springappuserservice.service.AuthService;

@RestController
@RequestMapping("/api/sessions")
public class SessionController {

    private final AuthService authService;

    public SessionController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/active")
    public ResponseEntity<Map<String, Object>> getActiveSessions() {
        return ResponseEntity.ok(authService.getActiveSessions());
    }

    @PostMapping("/{sessionId}/terminate")
    public ResponseEntity<Map<String, String>> terminateSession(@PathVariable String sessionId) {
        if (authService.terminateSession(sessionId)) {
            return ResponseEntity.ok(Map.of("message", "Session terminated"));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Session not found"));
    }

    @GetMapping("/validate/{sessionId}")
    public ResponseEntity<Map<String, Boolean>> validateSession(@PathVariable String sessionId) {
        return ResponseEntity.ok(Map.of("valid", authService.validateSession(sessionId)));
    }
}
