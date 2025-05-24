package com.example.ECommerce.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PersonalizedOfferDTO {
    private Long id;
    private String userId;
    private String productName;
    private int quantity;
    private String deliveryTerms;
    private String paymentTerms;
    private String additionalNotes;
    private LocalDateTime createdAt;
    private String status;
    private String managerResponse;
    private Double proposedPrice;
}