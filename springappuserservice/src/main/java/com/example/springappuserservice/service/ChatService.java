package com.example.springappuserservice.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.springappuserservice.model.ChatMessage;
import com.example.springappuserservice.model.ChatSession;
import com.example.springappuserservice.model.Role;
import com.example.springappuserservice.repository.ChatRepository;
import com.example.springappuserservice.repository.ChatSessionRepository;

@Service
public class ChatService {

    private final ChatRepository chatRepository;
    private final ChatSessionRepository chatSessionRepository;

    public ChatService(ChatRepository chatRepository, ChatSessionRepository chatSessionRepository) {
        this.chatRepository = chatRepository;
        this.chatSessionRepository = chatSessionRepository;
    }

    public ChatMessage sendMessage(ChatMessage message) {
        ChatSession session = chatSessionRepository.findById(message.getChatSessionId())
                .orElseGet(() -> new ChatSession());
        if (message.getChatSessionId() != null) {
            session.setSessionId(message.getChatSessionId());
        }
        if (message.getSenderRole() == Role.ACCOUNT_HOLDER) {
            session.setCustomerId(message.getSenderId());
        } else {
            session.setAgentId(message.getSenderId());
        }
        session.setLastMessage(message.getMessage());
        session.setLastMessageTime(LocalDateTime.now());
        session.setUnreadCount((session.getUnreadCount() == null ? 0 : session.getUnreadCount()) + 1);
        ChatSession savedSession = chatSessionRepository.save(session);
        message.setChatSessionId(savedSession.getSessionId());
        return chatRepository.save(message);
    }

    public List<ChatMessage> getAllMessages() {
        return chatRepository.findAll();
    }

    public List<ChatMessage> getSessionMessages(String chatSessionId) {
        return chatRepository.findByChatSessionIdOrderByCreatedAtAsc(chatSessionId);
    }

    public List<ChatSession> getUnrepliedSessions() {
        return chatSessionRepository.findByAgentIdIsNullAndIsActiveTrueOrderByLastMessageTimeDesc();
    }
}
