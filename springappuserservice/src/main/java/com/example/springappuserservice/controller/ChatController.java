package com.example.springappuserservice.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.springappuserservice.model.ChatMessage;
import com.example.springappuserservice.model.ChatSession;
import com.example.springappuserservice.service.ChatService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/send")
    public ResponseEntity<ChatMessage> sendMessage(@Valid @RequestBody ChatMessage message) {
        return ResponseEntity.status(HttpStatus.CREATED).body(chatService.sendMessage(message));
    }

    @GetMapping("/messages")
    public ResponseEntity<List<ChatMessage>> getAllMessages() {
        return ResponseEntity.ok(chatService.getAllMessages());
    }

    @GetMapping("/{chatSessionId}")
    public ResponseEntity<List<ChatMessage>> getSessionMessages(@PathVariable String chatSessionId) {
        return ResponseEntity.ok(chatService.getSessionMessages(chatSessionId));
    }

    @GetMapping("/unreplied")
    public ResponseEntity<List<ChatSession>> getUnrepliedMessages() {
        return ResponseEntity.ok(chatService.getUnrepliedSessions());
    }
}
