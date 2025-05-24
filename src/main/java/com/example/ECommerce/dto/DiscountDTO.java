package com.example.ECommerce.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class DiscountDTO {
    private Long discountId;
    private Long productId;
    private String productName;
    private Float discountPercent;
    private LocalDate startDate;
    private LocalDate endDate;
} 