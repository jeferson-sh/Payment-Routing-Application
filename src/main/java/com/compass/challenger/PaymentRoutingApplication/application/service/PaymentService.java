package com.compass.challenger.PaymentRoutingApplication.application.service;

import com.compass.challenger.PaymentRoutingApplication.application.dto.payment.PaymentRequest;

public interface PaymentService {
    PaymentRequest processPayment(PaymentRequest request);
}
