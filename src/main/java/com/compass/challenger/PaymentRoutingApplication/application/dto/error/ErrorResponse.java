package com.compass.challenger.PaymentRoutingApplication.application.dto.error;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonPropertyOrder({ "error", "message" })
public class ErrorResponse {
    @JsonProperty("error")
    private ErrorType errorType;
    private String message;
}
