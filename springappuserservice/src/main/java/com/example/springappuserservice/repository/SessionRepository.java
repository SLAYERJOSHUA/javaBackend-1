package com.example.springappuserservice.repository;

import java.util.Optional;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.springappuserservice.model.Session;

public interface SessionRepository extends JpaRepository<Session, Long> {

    Optional<Session> findByTokenAndIsActiveTrue(String token);

    Optional<Session> findBySessionIdAndIsActiveTrue(String sessionId);

    List<Session> findByIsActiveTrueAndExpiresAtAfter(LocalDateTime now);
}
