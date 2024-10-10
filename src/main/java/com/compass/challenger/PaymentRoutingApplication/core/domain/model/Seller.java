package com.compass.challenger.PaymentRoutingApplication.core.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "sellers")
public class Seller extends Client {
    @CreatedDate
    @Column
    private LocalDate dateContract;

}
