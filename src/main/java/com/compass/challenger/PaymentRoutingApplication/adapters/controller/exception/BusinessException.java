package com.compass.challenger.PaymentRoutingApplication.adapters.controller.exception;

import com.compass.challenger.PaymentRoutingApplication.application.dto.error.ErrorType;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessException extends RuntimeException {
    private final ErrorType errorType;
    private final HttpStatus httpStatus;

    public BusinessException(ErrorType errorType, String message, HttpStatus httpStatus) {
        super(message);
        this.errorType = errorType;
        this.httpStatus = httpStatus;
    }
}
