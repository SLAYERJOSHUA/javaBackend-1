package com.example.springapppaymentservice.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.springapppaymentservice.model.Payment;
import com.example.springapppaymentservice.model.PaymentMethod;
import com.example.springapppaymentservice.model.PaymentStatus;
import com.example.springapppaymentservice.repository.PaymentRepository;

@Service
public class PaymentService {

    private static final Random RANDOM = new Random();
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public Payment processPayment(Payment payment) {
        payment.setPaymentId(null);
        payment.setPaymentNumber(generatePaymentNumber());
        if (payment.getPaymentStatus() == null) {
            payment.setPaymentStatus(PaymentStatus.PENDING);
        }
        return paymentRepository.save(payment);
    }

    public Optional<Payment> getPaymentById(Long paymentId) {
        return paymentRepository.findById(paymentId);
    }

    public Page<Payment> getAllPayments(Pageable pageable) {
        return paymentRepository.findAll(pageable);
    }

    public Optional<Payment> updatePayment(Long paymentId, Payment details) {
        return paymentRepository.findById(paymentId).map(payment -> {
            if (details.getAccountId() != null) {
                payment.setAccountId(details.getAccountId());
            }
            if (details.getUserId() != null) {
                payment.setUserId(details.getUserId());
            }
            if (details.getAmount() != null) {
                payment.setAmount(details.getAmount());
            }
            if (details.getPaymentMethod() != null) {
                payment.setPaymentMethod(details.getPaymentMethod());
            }
            if (details.getPaymentStatus() != null) {
                applyStatus(payment, details.getPaymentStatus());
            }
            if (details.getTransactionReference() != null) {
                payment.setTransactionReference(details.getTransactionReference());
            }
            return paymentRepository.save(payment);
        });
    }

    public Optional<Payment> updatePaymentStatus(Long paymentId, PaymentStatus status) {
        return paymentRepository.findById(paymentId).map(payment -> {
            applyStatus(payment, status);
            return paymentRepository.save(payment);
        });
    }

    public List<Payment> getPaymentsByUser(Long userId) {
        return paymentRepository.findByUserId(userId);
    }

    public List<Payment> getPaymentsByAccount(Long accountId) {
        return paymentRepository.findByAccountId(accountId);
    }

    public List<Payment> filterPayments(PaymentStatus status, PaymentMethod method) {
        if (status != null) {
            return paymentRepository.findByPaymentStatus(status);
        }
        if (method != null) {
            return paymentRepository.findByPaymentMethod(method);
        }
        return paymentRepository.findAll();
    }

    private void applyStatus(Payment payment, PaymentStatus status) {
        payment.setPaymentStatus(status);
        if (status == PaymentStatus.COMPLETED && payment.getPaymentDate() == null) {
            payment.setPaymentDate(LocalDateTime.now());
        }
    }

    private String generatePaymentNumber() {
        String paymentNumber;
        do {
            paymentNumber = "PAY" + LocalDateTime.now().format(FORMATTER) + RANDOM.nextInt(1000);
        } while (paymentRepository.existsByPaymentNumber(paymentNumber));
        return paymentNumber;
    }
}
