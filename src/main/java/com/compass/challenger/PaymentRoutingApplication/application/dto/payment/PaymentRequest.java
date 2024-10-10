package com.compass.challenger.PaymentRoutingApplication.application.dto.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {

    @JsonProperty("client_id")
    @NotNull(message = "Client ID cannot be null")
    @Positive(message = "Client ID must be a positive number")
    private Long clientId;

    @JsonProperty("payment_items")
    @NotEmpty(message = "Payment items cannot be empty")
    @Valid
    private List<PaymentItemRequest> paymentItems;
}
