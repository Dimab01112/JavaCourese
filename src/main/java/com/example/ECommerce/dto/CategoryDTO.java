package com.example.ECommerce.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CategoryDTO {
    private Long categoryId;
    private String categoryName;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int productCount;
}
