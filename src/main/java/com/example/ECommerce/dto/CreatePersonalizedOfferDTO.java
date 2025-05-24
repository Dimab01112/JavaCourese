package com.example.ECommerce.dto;

import lombok.Data;

@Data
public class CreatePersonalizedOfferDTO {
    private String productName;
    private int quantity;
    private String deliveryTerms;
    private String paymentTerms;
    private String additionalNotes;
} 