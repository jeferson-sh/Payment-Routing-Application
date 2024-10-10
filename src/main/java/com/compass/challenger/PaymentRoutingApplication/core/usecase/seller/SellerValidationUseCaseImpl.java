package com.compass.challenger.PaymentRoutingApplication.core.usecase.seller;

import com.compass.challenger.PaymentRoutingApplication.adapters.controller.exception.BusinessException;
import com.compass.challenger.PaymentRoutingApplication.application.dto.error.ErrorType;
import com.compass.challenger.PaymentRoutingApplication.infrastructure.repository.SellerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SellerValidationUseCaseImpl implements SellerValidationUseCase {

    private final SellerRepository repository;

    @Override
    public void findClientById(Long clientId) {
        this.repository.findById(clientId).orElseThrow(()->
                new BusinessException(ErrorType.SELLER_NOT_FOUND, "Seller Not Found", HttpStatus.NOT_FOUND));
    }
}
