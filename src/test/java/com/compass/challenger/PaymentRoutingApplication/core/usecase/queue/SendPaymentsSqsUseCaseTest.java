package com.compass.challenger.PaymentRoutingApplication.core.usecase.queue;

import com.compass.challenger.PaymentRoutingApplication.application.dto.payment.PaymentItemRequest;
import com.compass.challenger.PaymentRoutingApplication.core.domain.constants.PaymentType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.test.context.ActiveProfiles;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@EnableRetry
@ActiveProfiles("test")
class SendPaymentsSqsUseCaseTest {

    @Autowired
    private SendPaymentsSqsUseCase sendPaymentsSqsUseCase;

    @MockBean
    private SqsClient sqsClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should retry sending message to SQS on failure")
    void testRetryOnFailure() {
        var paymentItemRequest = new PaymentItemRequest(1L, new BigDecimal("100.00"), PaymentType.PARTIAL);
        var paymentStatus = PaymentType.PARTIAL;
        var sqsFailed = new RuntimeException("SQS failed");
        when(this.sqsClient.sendMessage(any(SendMessageRequest.class))).thenThrow(sqsFailed);
        try {
            sendPaymentsSqsUseCase.sendMessageToQueueByStatus(paymentItemRequest, paymentStatus);
        }catch (Exception ex){
            assertNotNull(ex.getMessage());
        }
        verify(sqsClient, times(3)).sendMessage(any(SendMessageRequest.class));
    }

    @Test
    @DisplayName("Should send message to SQS with one retry and one success")
    void testSuccessWithoutRetry() throws Exception {
        var paymentItemRequest = new PaymentItemRequest(1L, new BigDecimal("100.00"), PaymentType.TOTAL);
        var paymentStatus = PaymentType.TOTAL;
        var sqsFailed = new RuntimeException("SQS failed");
        when(this.sqsClient.sendMessage(any(SendMessageRequest.class)))
                .thenThrow(sqsFailed)
                .thenReturn(SendMessageResponse.builder().build());
        sendPaymentsSqsUseCase.sendMessageToQueueByStatus(paymentItemRequest, paymentStatus);
        verify(sqsClient, times(2)).sendMessage(any(SendMessageRequest.class));
    }
}
