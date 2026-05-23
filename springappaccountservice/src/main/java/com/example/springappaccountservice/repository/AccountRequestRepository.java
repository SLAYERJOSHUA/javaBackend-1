package com.example.springappaccountservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.springappaccountservice.model.AccountRequest;
import com.example.springappaccountservice.model.RequestStatus;

public interface AccountRequestRepository extends JpaRepository<AccountRequest, Long> {

    List<AccountRequest> findByUserId(Long userId);

    List<AccountRequest> findByStatus(RequestStatus status);

    List<AccountRequest> findByUserIdAndStatus(Long userId, RequestStatus status);

    List<AccountRequest> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<AccountRequest> findAllByOrderByCreatedAtDesc();

    boolean existsByUserIdAndStatus(Long userId, RequestStatus status);
}
