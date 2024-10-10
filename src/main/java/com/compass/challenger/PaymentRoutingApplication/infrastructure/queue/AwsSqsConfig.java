package com.compass.challenger.PaymentRoutingApplication.infrastructure.queue;

import com.compass.challenger.PaymentRoutingApplication.core.domain.constants.PaymentType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

import java.net.URI;
import java.util.Map;

@RequiredArgsConstructor
@Configuration
public class AwsSqsConfig {

    private final AwsSqsProperties properties;

    @Bean
    public SqsClient sqsClient() {
        var builder = SqsClient.builder()
                .region(Region.US_EAST_1);
        return (this.properties.isLocalstackEnable())
                ? builder.endpointOverride(URI.create("http://localhost:4566")).build()
                : builder.build();
    }

    @Bean
    public Map<PaymentType, String> paymentTypeQueueMap() {
        return this.properties.getPaymentTypeQueueMap();
    }

}
