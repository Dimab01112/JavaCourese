package com.example.ECommerce.dto;

import lombok.Data;

@Data
public class WishlistItemDTO {
    private Long wishlistItemId;
    private String keycloakUserId;
    private Long productId;
    private String productName;
    private Float price;
    private String imageUrl;
} 