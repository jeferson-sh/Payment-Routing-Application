package com.compass.challenger.PaymentRoutingApplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.retry.annotation.EnableRetry;

@EnableJpaAuditing
@EnableRetry
@SpringBootApplication
public class PaymentRoutingApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaymentRoutingApplication.class, args);
	}

}
