package com.compass.challenger.PaymentRoutingApplication.core.usecase.payment;

import com.compass.challenger.PaymentRoutingApplication.core.domain.constants.PaymentType;
import com.compass.challenger.PaymentRoutingApplication.core.domain.model.PaymentItem;

import java.math.BigDecimal;

public interface PaymentItemValidationUseCase {

    PaymentItem findPaymentItemById(Long paymentItemId);
    PaymentType determinePaymentStatus(PaymentItem paymentItem, BigDecimal paymentValue);
}
