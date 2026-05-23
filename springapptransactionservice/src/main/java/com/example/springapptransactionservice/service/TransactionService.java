package com.example.springapptransactionservice.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.springapptransactionservice.model.Transaction;
import com.example.springapptransactionservice.model.TransactionStatus;
import com.example.springapptransactionservice.model.TransactionType;
import com.example.springapptransactionservice.repository.TransactionRepository;

@Service
public class TransactionService {

    private static final Random RANDOM = new Random();
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Transaction createTransaction(Transaction transaction) {
        transaction.setTransactionId(null);
        transaction.setTransactionNumber(generateTransactionNumber());
        if (transaction.getStatus() == null) {
            transaction.setStatus(TransactionStatus.PENDING);
        }
        return transactionRepository.save(transaction);
    }

    public Optional<Transaction> getTransactionById(Long transactionId) {
        return transactionRepository.findById(transactionId);
    }

    public Page<Transaction> getAllTransactions(Pageable pageable) {
        return transactionRepository.findAll(pageable);
    }

    public List<Transaction> getTransactionsByAccount(Long accountId) {
        return transactionRepository.findByAccountId(accountId);
    }

    public List<Transaction> getTransactionsByUser(Long userId) {
        return transactionRepository.findByUserId(userId);
    }

    public Optional<Transaction> updateTransactionStatus(Long transactionId, TransactionStatus status) {
        return transactionRepository.findById(transactionId)
                .map(transaction -> {
                    transaction.setStatus(status);
                    return transactionRepository.save(transaction);
                });
    }

    public List<Transaction> filterTransactions(TransactionStatus status, TransactionType type) {
        if (status != null) {
            return transactionRepository.findByStatus(status);
        }
        if (type != null) {
            return transactionRepository.findByTransactionType(type);
        }
        return transactionRepository.findAll();
    }

    private String generateTransactionNumber() {
        String transactionNumber;
        do {
            transactionNumber = "TXN" + LocalDateTime.now().format(FORMATTER) + RANDOM.nextInt(1000);
        } while (transactionRepository.existsByTransactionNumber(transactionNumber));
        return transactionNumber;
    }
}
