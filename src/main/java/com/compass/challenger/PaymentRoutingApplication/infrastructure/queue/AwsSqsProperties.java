package com.compass.challenger.PaymentRoutingApplication.infrastructure.queue;

import com.compass.challenger.PaymentRoutingApplication.core.domain.constants.PaymentType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.EnumMap;
import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "aws.sqs")
public class AwsSqsProperties {

    private boolean localstackEnable;

    private QueueProperties queue;

    @Data
    public static class QueueProperties {
        private String partial;
        private String total;
        private String exceed;
    }

    public Map<PaymentType, String> getPaymentTypeQueueMap() {
        Map<PaymentType, String> map = new EnumMap<>(PaymentType.class);
        map.put(PaymentType.PARTIAL, this.queue.getPartial());
        map.put(PaymentType.TOTAL, this.queue.getTotal());
        map.put(PaymentType.EXCEED, this.queue.getExceed());
        return map;
    }
}
