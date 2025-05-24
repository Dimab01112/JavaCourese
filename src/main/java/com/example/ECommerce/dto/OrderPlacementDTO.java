package com.example.ECommerce.dto;

import lombok.Data;

@Data
public class OrderPlacementDTO {
    private String paymentMethod;
    private String shippingAddress;
    private String billingAddress;
} 