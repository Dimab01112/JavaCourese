package com.example.ECommerce.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "discounts")
public class Discount {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long discountId;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private Float discountPercent;
    private LocalDate startDate;
    private LocalDate endDate;

    // getters and setters
}
