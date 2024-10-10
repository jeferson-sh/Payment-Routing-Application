package com.compass.challenger.PaymentRoutingApplication.application.service;

import com.compass.challenger.PaymentRoutingApplication.application.dto.payment.PaymentRequest;
import com.compass.challenger.PaymentRoutingApplication.core.usecase.payment.PaymentItemValidationUseCase;
import com.compass.challenger.PaymentRoutingApplication.core.usecase.queue.SendPaymentsSqsUseCase;
import com.compass.challenger.PaymentRoutingApplication.core.usecase.seller.SellerValidationUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PaymentServiceImpl implements PaymentService{

    private final SellerValidationUseCase sellerUseCase;
    private final PaymentItemValidationUseCase paymentItemValidationUseCase;
    private final SendPaymentsSqsUseCase sendPaymentsSqsUseCase;

    @Override
    public PaymentRequest processPayment(PaymentRequest request) {
        this.sellerUseCase.findClientById(request.getClientId());
        for (var itemRequest : request.getPaymentItems()) {
            var paymentItem = this.paymentItemValidationUseCase.findPaymentItemById(
                    itemRequest.getPaymentId());
            var status = this.paymentItemValidationUseCase.determinePaymentStatus(
                    paymentItem, itemRequest.getPaymentValue());
            this.sendPaymentsSqsUseCase.sendMessageToQueueByStatus(itemRequest, status);
        }
        return request;
    }
}
