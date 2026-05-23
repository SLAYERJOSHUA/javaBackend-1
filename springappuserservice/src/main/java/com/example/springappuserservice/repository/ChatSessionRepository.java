package com.example.springappuserservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.springappuserservice.model.ChatSession;

public interface ChatSessionRepository extends JpaRepository<ChatSession, String> {

    List<ChatSession> findByAgentIdIsNullAndIsActiveTrueOrderByLastMessageTimeDesc();
}
