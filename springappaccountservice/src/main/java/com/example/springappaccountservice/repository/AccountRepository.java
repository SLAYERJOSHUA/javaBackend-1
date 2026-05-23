package com.example.springappaccountservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.springappaccountservice.model.Account;
import com.example.springappaccountservice.model.AccountStatus;

public interface AccountRepository extends JpaRepository<Account, Long> {

    boolean existsByAccountNumber(String accountNumber);

    Optional<Account> findByAccountNumber(String accountNumber);

    List<Account> findByUserId(Long userId);

    List<Account> findByStatus(AccountStatus status);

    List<Account> findByUserIdAndStatus(Long userId, AccountStatus status);

    Page<Account> findByStatus(AccountStatus status, Pageable pageable);

    @Query("select account from Account account where account.status = com.example.springappaccountservice.model.AccountStatus.ACTIVE")
    List<Account> findActiveAccounts();

    boolean existsByUserIdAndStatus(Long userId, AccountStatus status);
}
