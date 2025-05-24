package com.example.ECommerce.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReviewDTO {
    private Long id;
    private String keycloakUserId;
    private Long productId;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
    private String userName;
} 