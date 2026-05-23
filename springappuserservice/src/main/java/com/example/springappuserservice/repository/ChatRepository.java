package com.example.springappuserservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.springappuserservice.model.ChatMessage;

public interface ChatRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findByChatSessionIdOrderByCreatedAtAsc(String chatSessionId);
}
