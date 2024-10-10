package com.compass.challenger.PaymentRoutingApplication.infrastructure.repository;

import com.compass.challenger.PaymentRoutingApplication.core.domain.model.PaymentItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentItemRepository extends JpaRepository<PaymentItem, Long> {
}