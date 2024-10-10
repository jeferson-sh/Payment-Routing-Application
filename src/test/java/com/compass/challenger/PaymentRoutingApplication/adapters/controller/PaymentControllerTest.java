package com.compass.challenger.PaymentRoutingApplication.adapters.controller;

import com.compass.challenger.PaymentRoutingApplication.application.dto.payment.PaymentItemRequest;
import com.compass.challenger.PaymentRoutingApplication.application.dto.payment.PaymentRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SqsClient client;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should return error when clientId is null")
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
    @DisplayName("Should return error when clientId is negative")
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
    @DisplayName("Should return error when paymentItems is empty")
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
    @DisplayName("Should return error when paymentId is null")
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
    @DisplayName("Should return error when paymentId is negative")
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
    @DisplayName("Should return error when paymentValue is null")
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
    @DisplayName("Should return error when paymentValue is zero")
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
    @DisplayName("Should successfully process partial payment")
    void testPartialPaymentSuccess() throws Exception {
        var paymentItem = new PaymentItemRequest(1L, new BigDecimal("10.0"), null);
        var paymentRequest = new PaymentRequest(1L, List.of(paymentItem));
        when(this.client.sendMessage(any(SendMessageRequest.class))).thenReturn(SendMessageResponse.builder().build());
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
    @DisplayName("Should successfully process total payment")
    void testTotalPaymentSuccess() throws Exception {
        var paymentItem = new PaymentItemRequest(1L, new BigDecimal("100.0"), null);
        var paymentRequest = new PaymentRequest(1L, List.of(paymentItem));
        when(this.client.sendMessage(any(SendMessageRequest.class))).thenReturn(SendMessageResponse.builder().build());
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
    @DisplayName("Should successfully process exceeded payment")
    void testExceededPaymentSuccess() throws Exception {
        var paymentItem = new PaymentItemRequest(1L, new BigDecimal("150.0"), null);
        var paymentRequest = new PaymentRequest(1L, List.of(paymentItem));
        when(this.client.sendMessage(any(SendMessageRequest.class))).thenReturn(SendMessageResponse.builder().build());
        mockMvc.perform(put("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.client_id", is(1)))
                .andExpect(jsonPath("$.payment_items[0].payment_id", is(1)))
                .andExpect(jsonPath("$.payment_items[0].payment_value", is(150.0)))
                .andExpect(jsonPath("$.payment_items[0].payment_status", is("EXCEED")));
    }

    @Test
    @DisplayName("Should return SELLER_NOT_FOUND error when client_id is not found")
    void testSellerNotFound() throws Exception {
        var paymentItem = new PaymentItemRequest(1L, new BigDecimal("10.0"), null);
        var paymentRequest = new PaymentRequest(10L, List.of(paymentItem));

        mockMvc.perform(put("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("SELLER_NOT_FOUND")))
                .andExpect(jsonPath("$.message", is("Seller Not Found")));
    }

    @Test
    @DisplayName("Should return PAYMENT_ITEM_NOT_FOUND error when payment_id is not found")
    void testPaymentItemNotFound() throws Exception {
        var paymentItem = new PaymentItemRequest(10L, new BigDecimal("10.0"), null);
        var paymentRequest = new PaymentRequest(1L, List.of(paymentItem));

        mockMvc.perform(put("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("PAYMENT_ITEM_NOT_FOUND")))
                .andExpect(jsonPath("$.message", is("Payment item with id: 10 not found")));
    }

    @Test
    @DisplayName("Should return GENERIC_ERROR for NoResourceFoundException when accessing invalid static resource")
    void testGenericErrorForInvalidResource() throws Exception {
        mockMvc.perform(put("/api/v1/payment"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error", is("GENERIC_ERROR")))
                .andExpect(jsonPath("$.message", is("org.springframework.web.servlet.resource.NoResourceFoundException: No static resource api/v1/payment.: No static resource api/v1/payment.")));
    }
}
