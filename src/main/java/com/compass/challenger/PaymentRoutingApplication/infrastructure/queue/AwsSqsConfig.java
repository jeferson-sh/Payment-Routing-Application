package com.compass.challenger.PaymentRoutingApplication.infrastructure.queue;

import com.compass.challenger.PaymentRoutingApplication.core.domain.constants.PaymentType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

import java.net.URI;
import java.util.EnumMap;
import java.util.Map;

@Configuration
public class AwsSqsConfig {

    @Value("${aws.sqs.queue.partial}")
    private String partialQueueUrl;

    @Value("${aws.sqs.queue.total}")
    private String totalQueueUrl;

    @Value("${aws.sqs.queue.exceed}")
    private String exceedingQueueUrl;

    @Value("${aws.sqs.localstack.enable}")
    private boolean localStackIsEnable;

    @Bean
    @Profile("local")
    public SqsClient sqsClient() {
        var builder = SqsClient.builder()
                .region(Region.US_EAST_1);
        return (this.localStackIsEnable)
                ? builder.endpointOverride(URI.create("http://localhost:4566")).build()
                : builder.build();
    }

    @Bean
    public Map<PaymentType, String> paymentTypeQueueMap() {
        Map<PaymentType, String> map = new EnumMap<>(PaymentType.class);
        map.put(PaymentType.PARTIAL, partialQueueUrl);
        map.put(PaymentType.TOTAL, totalQueueUrl);
        map.put(PaymentType.EXCEED, exceedingQueueUrl);
        return map;
    }

}
