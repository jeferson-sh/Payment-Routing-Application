package com.compass.challenger.PaymentRoutingApplication.adapters.controller;

import com.compass.challenger.PaymentRoutingApplication.application.dto.payment.PaymentItemRequest;
import com.compass.challenger.PaymentRoutingApplication.application.dto.payment.PaymentRequest;
import com.compass.challenger.PaymentRoutingApplication.core.domain.constants.PaymentType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentControllerErrorTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testPaymentRequestWithNullClientId() throws Exception {
        var paymentRequest = new PaymentRequest(null, Collections.emptyList());

        mockMvc.perform(put("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("VALIDATION_ERROR")))
                .andExpect(jsonPath("$.message", is("Validation failed for some fields.")))
                .andExpect(jsonPath("$.details.clientId", is("Client ID cannot be null")));
    }

    @Test
    void testPaymentRequestWithNegativeClientId() throws Exception {
        var paymentRequest = new PaymentRequest(-1L, Collections.emptyList());

        mockMvc.perform(put("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("VALIDATION_ERROR")))
                .andExpect(jsonPath("$.message", is("Validation failed for some fields.")))
                .andExpect(jsonPath("$.details.clientId", is("Client ID must be a positive number")));
    }

    @Test
    void testPaymentRequestWithEmptyPaymentItems() throws Exception {
        var paymentRequest = new PaymentRequest(1L, Collections.emptyList());

        mockMvc.perform(put("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("VALIDATION_ERROR")))
                .andExpect(jsonPath("$.message", is("Validation failed for some fields.")))
                .andExpect(jsonPath("$.details.paymentItems", is("Payment items cannot be empty")));
    }

    @Test
    void testPaymentItemRequestWithNullPaymentId() throws Exception {
        var paymentItem = new PaymentItemRequest(null, new BigDecimal("100.00"), null);
        var paymentRequest = new PaymentRequest(1L, List.of(paymentItem));

        mockMvc.perform(put("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("VALIDATION_ERROR")))
                .andExpect(jsonPath("$.message", is("Validation failed for some fields.")))
                .andExpect(jsonPath("$.details['paymentItems[0].paymentId']", is("Payment ID cannot be null")));
    }

    @Test
    void testPaymentItemRequestWithNegativePaymentId() throws Exception {
        var paymentItem = new PaymentItemRequest(-1L, new BigDecimal("100.00"), null);
        var paymentRequest = new PaymentRequest(1L, List.of(paymentItem));

        mockMvc.perform(put("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("VALIDATION_ERROR")))
                .andExpect(jsonPath("$.message", is("Validation failed for some fields.")))
                .andExpect(jsonPath("$.details['paymentItems[0].paymentId']", is("Payment ID must be a positive number")));
    }

    @Test
    void testPaymentItemRequestWithNullPaymentValue() throws Exception {
        var paymentItem = new PaymentItemRequest(1L, null, null);
        var paymentRequest = new PaymentRequest(1L, List.of(paymentItem));

        mockMvc.perform(put("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("VALIDATION_ERROR")))
                .andExpect(jsonPath("$.message", is("Validation failed for some fields.")))
                .andExpect(jsonPath("$.details['paymentItems[0].paymentValue']", is("Payment value cannot be null")));
    }

    @Test
    void testPaymentItemRequestWithZeroPaymentValue() throws Exception {
        var paymentItem = new PaymentItemRequest(1L, new BigDecimal("0.0"), null);
        var paymentRequest = new PaymentRequest(1L, List.of(paymentItem));

        mockMvc.perform(put("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("VALIDATION_ERROR")))
                .andExpect(jsonPath("$.message", is("Validation failed for some fields.")))
                .andExpect(jsonPath("$.details['paymentItems[0].paymentValue']", is("Payment value must be greater than zero")));
    }

    @Test
    void testPartialPaymentSuccess() throws Exception {
        var paymentItem = new PaymentItemRequest(1L, new BigDecimal("10.0"), PaymentType.PARTIAL);
        var paymentRequest = new PaymentRequest(1L, List.of(paymentItem));

        mockMvc.perform(put("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.client_id", is(1)))
                .andExpect(jsonPath("$.payment_items[0].payment_id", is(1)))
                .andExpect(jsonPath("$.payment_items[0].payment_value", is(10.0)))
                .andExpect(jsonPath("$.payment_items[0].payment_status", is("PARTIAL")));
    }

    @Test
    void testTotalPaymentSuccess() throws Exception {
        var paymentItem = new PaymentItemRequest(1L, new BigDecimal("100.0"), PaymentType.TOTAL);
        var paymentRequest = new PaymentRequest(1L, List.of(paymentItem));

        mockMvc.perform(put("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.client_id", is(1)))
                .andExpect(jsonPath("$.payment_items[0].payment_id", is(1)))
                .andExpect(jsonPath("$.payment_items[0].payment_value", is(100.0)))
                .andExpect(jsonPath("$.payment_items[0].payment_status", is("TOTAL")));
    }

    @Test
    void testExceededPaymentSuccess() throws Exception {
        var paymentItem = new PaymentItemRequest(1L, new BigDecimal("150.0"), PaymentType.EXCEED);
        var paymentRequest = new PaymentRequest(1L, List.of(paymentItem));

        mockMvc.perform(put("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.client_id", is(1)))
                .andExpect(jsonPath("$.payment_items[0].payment_id", is(1)))
                .andExpect(jsonPath("$.payment_items[0].payment_value", is(150.0)))
                .andExpect(jsonPath("$.payment_items[0].payment_status", is("EXCEED")));
    }
}
