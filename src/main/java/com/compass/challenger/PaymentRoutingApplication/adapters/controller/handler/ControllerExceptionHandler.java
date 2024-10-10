package com.compass.challenger.PaymentRoutingApplication.adapters.controller.handler;

import com.compass.challenger.PaymentRoutingApplication.adapters.controller.exception.BusinessException;
import com.compass.challenger.PaymentRoutingApplication.application.dto.error.ErrorResponse;
import com.compass.challenger.PaymentRoutingApplication.application.dto.error.ErrorType;
import com.compass.challenger.PaymentRoutingApplication.application.dto.error.ValidationErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(BusinessException ex) {
        return ResponseEntity.status(ex.getHttpStatus())
                .body(new ErrorResponse(ex.getErrorType(), ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        var message = (ex != null ? ex.toString() : "Unknown exception")
                + (ex != null && ex.getMessage() != null ? ": " + ex.getMessage() : "");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(ErrorType.GENERIC_ERROR, message));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        var errorResponse = new ValidationErrorResponse(ErrorType.VALIDATION_ERROR, "Validation failed for some fields.", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

}
