package com.example.ECommerce.dto;

import lombok.Data;

@Data
public class CartItemDTO {
    private Long id;
    private Long productId;
    private String productName;
    private int quantity;
    private float price;
    private float subtotal;
    private String imageUrl;
} 