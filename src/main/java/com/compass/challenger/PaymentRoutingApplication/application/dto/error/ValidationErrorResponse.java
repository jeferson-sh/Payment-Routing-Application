package com.compass.challenger.PaymentRoutingApplication.application.dto.error;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ValidationErrorResponse extends ErrorResponse {
    private Map<String, String> details;

    public ValidationErrorResponse(ErrorType errorType, String message, Map<String, String> details) {
        super(errorType, message);
        this.details = details;
    }
}
