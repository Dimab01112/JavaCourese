package com.example.ECommerce.dto;

import lombok.Data;

@Data
public class AddToCartDTO {
    private Long productId;
    private Integer quantity;
} 