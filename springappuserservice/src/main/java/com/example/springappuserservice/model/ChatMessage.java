package com.example.springappuserservice.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "chat_messages")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "senderId is required")
    @Column(nullable = false)
    private Long senderId;

    @NotNull(message = "senderRole is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role senderRole;

    private Long receiverId;

    @Enumerated(EnumType.STRING)
    private Role receiverRole;

    @NotBlank(message = "message is required")
    @Lob
    @Column(nullable = false)
    private String message;

    private String chatSessionId;

    @Column(nullable = false)
    private Boolean isRead = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
        if (isRead == null) {
            isRead = false;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public Role getSenderRole() {
        return senderRole;
    }

    public void setSenderRole(Role senderRole) {
        this.senderRole = senderRole;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public Role getReceiverRole() {
        return receiverRole;
    }

    public void setReceiverRole(Role receiverRole) {
        this.receiverRole = receiverRole;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getChatSessionId() {
        return chatSessionId;
    }

    public void setChatSessionId(String chatSessionId) {
        this.chatSessionId = chatSessionId;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
