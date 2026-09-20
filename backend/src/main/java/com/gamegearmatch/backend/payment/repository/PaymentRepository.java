package com.gamegearmatch.backend.payment.repository;

import com.gamegearmatch.backend.payment.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    boolean existsByPaymentKey(String paymentKey);
}
