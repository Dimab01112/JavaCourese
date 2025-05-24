package com.example.ECommerce.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "personalized_offers")
public class PersonalizedOffer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "keycloak_user_id")
    private String keycloakUserId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(columnDefinition = "FLOAT")
    private Float discountPercentage;

    private LocalDateTime validFrom;
    private LocalDateTime validTo;
    private boolean isUsed;

    private String productName;
    private Integer quantity;
    private String deliveryTerms;
    private String paymentTerms;
    private String additionalNotes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    private OfferStatus status;

    private String managerResponse;

    @Column(columnDefinition = "FLOAT")
    private Float proposedPrice;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
        if (status == null) {
            status = OfferStatus.PENDING;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum OfferStatus {
        PENDING,
        APPROVED,
        REJECTED
    }
} 