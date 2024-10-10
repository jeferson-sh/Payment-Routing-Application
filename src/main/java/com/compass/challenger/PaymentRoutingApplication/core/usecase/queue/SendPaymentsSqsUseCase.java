package com.compass.challenger.PaymentRoutingApplication.core.usecase.queue;

import com.compass.challenger.PaymentRoutingApplication.application.dto.payment.PaymentItemRequest;
import com.compass.challenger.PaymentRoutingApplication.core.domain.constants.PaymentType;

public interface SendPaymentsSqsUseCase {

    void sendMessageToQueueByStatus(PaymentItemRequest paymentItemRequest, PaymentType paymentStatus) ;
}
