package com.compass.challenger.PaymentRoutingApplication.adapters.controller;

import com.compass.challenger.PaymentRoutingApplication.application.dto.payment.PaymentRequest;
import com.compass.challenger.PaymentRoutingApplication.application.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/payments")
public class PaymentController implements PaymentControllerOperation {

    private final PaymentService service;

    @PutMapping
    public ResponseEntity<PaymentRequest> setPayment(@RequestBody @Valid PaymentRequest paymentRequest) {
        var paymentProcessed = this.service.processPayment(paymentRequest);
        return ResponseEntity.ok(paymentProcessed);
    }
}
