package com.compass.challenger.PaymentRoutingApplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class PaymentRoutingApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaymentRoutingApplication.class, args);
	}

}
