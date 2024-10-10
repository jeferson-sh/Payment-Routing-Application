package com.compass.challenger.PaymentRoutingApplication.core.usecase.queue;

import com.compass.challenger.PaymentRoutingApplication.adapters.controller.exception.BusinessException;
import com.compass.challenger.PaymentRoutingApplication.application.dto.error.ErrorType;
import com.compass.challenger.PaymentRoutingApplication.application.dto.payment.PaymentItemRequest;
import com.compass.challenger.PaymentRoutingApplication.core.domain.constants.PaymentType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.util.Map;

@RequiredArgsConstructor
@Component
public class SendPaymentsSqsUseCaseImpl implements SendPaymentsSqsUseCase {

    private final SqsClient sqsClient;
    private final ObjectMapper mapper;
    private final Map<PaymentType, String> paymentTypeSqsMap;

    @Override
    public void sendMessageToQueueByStatus(PaymentItemRequest paymentItemRequest, PaymentType paymentStatus) {
        try {
            var queueUrl = this.paymentTypeSqsMap.get(paymentStatus);
            paymentItemRequest.setPaymentStatus(paymentStatus);
            var messageBody = this.mapper.writeValueAsString(paymentItemRequest);
            var sendMessageRequest = SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageBody(messageBody)
                    .build();
            this.sqsClient.sendMessage(sendMessageRequest);
        } catch (Exception exception) {
            throw new BusinessException(ErrorType.SEND_MESSAGE_ERROR, exception.getLocalizedMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
