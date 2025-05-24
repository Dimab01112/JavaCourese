package com.example.ECommerce.mapper;

import com.example.ECommerce.dto.DiscountDTO;
import com.example.ECommerce.entity.Discount;
import com.example.ECommerce.entity.Product;
import com.example.ECommerce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DiscountMapper {
    
    @Autowired
    private ProductRepository productRepository;
    
    public DiscountDTO toDTO(Discount discount) {
        if (discount == null) {
            return null;
        }

        DiscountDTO dto = new DiscountDTO();
        dto.setDiscountId(discount.getDiscountId());
        dto.setProductId(discount.getProduct().getProductId());
        dto.setProductName(discount.getProduct().getName());
        dto.setDiscountPercent(discount.getDiscountPercent());
        dto.setStartDate(discount.getStartDate());
        dto.setEndDate(discount.getEndDate());
        return dto;
    }

    public Discount toEntity(DiscountDTO dto) {
        if (dto == null) {
            return null;
        }

        Discount discount = new Discount();
        discount.setDiscountId(dto.getDiscountId());
        discount.setDiscountPercent(dto.getDiscountPercent());
        discount.setStartDate(dto.getStartDate());
        discount.setEndDate(dto.getEndDate());
        
        // Set the Product entity
        Product product = productRepository.findById(dto.getProductId())
            .orElseThrow(() -> new RuntimeException("Product not found"));
        discount.setProduct(product);
        
        return discount;
    }

    public List<DiscountDTO> toDTOList(List<Discount> discounts) {
        if (discounts == null) {
            return null;
        }
        return discounts.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
} 