package com.example.springapppaymentservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.springapppaymentservice.model.Payment;
import com.example.springapppaymentservice.model.PaymentMethod;
import com.example.springapppaymentservice.model.PaymentStatus;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    boolean existsByPaymentNumber(String paymentNumber);

    List<Payment> findByUserId(Long userId);

    List<Payment> findByAccountId(Long accountId);

    List<Payment> findByPaymentStatus(PaymentStatus paymentStatus);

    List<Payment> findByPaymentMethod(PaymentMethod paymentMethod);
}
