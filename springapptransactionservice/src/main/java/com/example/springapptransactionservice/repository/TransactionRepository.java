package com.example.springapptransactionservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.springapptransactionservice.model.Transaction;
import com.example.springapptransactionservice.model.TransactionStatus;
import com.example.springapptransactionservice.model.TransactionType;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    boolean existsByTransactionNumber(String transactionNumber);

    List<Transaction> findByAccountId(Long accountId);

    List<Transaction> findByUserId(Long userId);

    List<Transaction> findByStatus(TransactionStatus status);

    List<Transaction> findByTransactionType(TransactionType transactionType);
}
