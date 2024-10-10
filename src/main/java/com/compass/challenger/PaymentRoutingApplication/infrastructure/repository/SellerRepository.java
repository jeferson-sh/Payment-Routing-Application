package com.compass.challenger.PaymentRoutingApplication.infrastructure.repository;

import com.compass.challenger.PaymentRoutingApplication.core.domain.model.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SellerRepository extends JpaRepository<Seller, Long> {
}