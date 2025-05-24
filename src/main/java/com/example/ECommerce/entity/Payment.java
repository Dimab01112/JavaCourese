package com.example.ECommerce.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(columnDefinition = "FLOAT")
    private Float amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", length = 20)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private LocalDateTime paymentDate;

    public enum PaymentMethod {
        CREDIT_CARD,
        DEBIT_CARD,
        PAYPAL,
        BANK_TRANSFER
    }

    public enum PaymentStatus {
        PENDING,
        COMPLETED,
        FAILED,
        REFUNDED
    }

    // getters and setters
}