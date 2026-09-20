package com.gamegearmatch.backend.payment.controller;

import com.gamegearmatch.backend.payment.dto.ConfirmPaymentRequest;
import com.gamegearmatch.backend.payment.dto.PaymentResponse;
import com.gamegearmatch.backend.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/confirm")
    public ResponseEntity<PaymentResponse> confirm(Authentication authentication, @Valid @RequestBody ConfirmPaymentRequest request) {
        return ResponseEntity.ok(paymentService.confirm(authentication.getName(), request));
    }
}
