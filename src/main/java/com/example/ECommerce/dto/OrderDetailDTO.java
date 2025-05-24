package com.example.ECommerce.dto;

import lombok.Data;

@Data
public class OrderDetailDTO {
    private Long orderDetailId;
    private Long orderId;
    private Long productId;
    private String productName;
    private Integer quantity;
    private Float price;
    private Float subtotal;
} 