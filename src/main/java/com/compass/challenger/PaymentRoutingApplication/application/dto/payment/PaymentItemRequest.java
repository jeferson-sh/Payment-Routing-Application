package com.compass.challenger.PaymentRoutingApplication.application.dto.payment;

import com.compass.challenger.PaymentRoutingApplication.core.domain.constants.PaymentType;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentItemRequest {

    @JsonProperty("payment_id")
    @NotNull(message = "Payment ID cannot be null")
    @Positive(message = "Payment ID must be a positive number")
    private Long paymentId;

    @JsonProperty("payment_value")
    @NotNull(message = "Payment value cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Payment value must be greater than zero")
    private BigDecimal paymentValue;

    @JsonProperty("payment_status")
    private PaymentType paymentStatus;
}
