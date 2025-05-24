package com.example.ECommerce.dto;

import lombok.Data;
import java.util.List;

@Data
public class ProductDTO {
    private Long productId;
    private String name;
    private String description;
    private Float price;
    private Float wholesalePrice;
    private Integer stock;
    private Long categoryId;
    private String categoryName;
    private List<ReviewDTO> reviews;
} 