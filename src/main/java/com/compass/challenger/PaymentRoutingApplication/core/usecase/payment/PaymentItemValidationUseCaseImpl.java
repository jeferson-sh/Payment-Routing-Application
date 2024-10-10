package com.compass.challenger.PaymentRoutingApplication.core.usecase.payment;

import com.compass.challenger.PaymentRoutingApplication.adapters.controller.exception.BusinessException;
import com.compass.challenger.PaymentRoutingApplication.application.dto.error.ErrorType;
import com.compass.challenger.PaymentRoutingApplication.core.domain.constants.PaymentType;
import com.compass.challenger.PaymentRoutingApplication.core.domain.model.PaymentItem;
import com.compass.challenger.PaymentRoutingApplication.infrastructure.repository.PaymentItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Component
public class PaymentItemValidationUseCaseImpl implements PaymentItemValidationUseCase {

    private final PaymentItemRepository repository;

    @Override
    public PaymentItem findPaymentItemById(Long paymentItemId) {
        return this.repository.findById(paymentItemId).orElseThrow(() ->
                new BusinessException(ErrorType.PAYMENT_ITEM_NOT_FOUND,
                        String.format("Payment item with id: %s not found", paymentItemId), HttpStatus.NOT_FOUND));
    }

    @Override
    public PaymentType determinePaymentStatus(PaymentItem paymentItem, BigDecimal paymentValue) {
        int comparisonResult = paymentValue.compareTo(paymentItem.getAmount());
        return switch (comparisonResult) {
            case -1 -> PaymentType.PARTIAL;
            case 0 -> PaymentType.TOTAL;
            default -> PaymentType.EXCEED;
        };
    }
}
