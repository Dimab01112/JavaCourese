package com.example.ECommerce.mapper;

import com.example.ECommerce.dto.ProductDTO;
import com.example.ECommerce.dto.ReviewDTO;
import com.example.ECommerce.entity.Product;
import com.example.ECommerce.entity.Category;
import com.example.ECommerce.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductMapper {

    @Autowired
    private ReviewMapper reviewMapper;

    @Autowired
    private CategoryRepository categoryRepository;

    public ProductDTO toDTO(Product product) {
        if (product == null) {
            return null;
        }

        ProductDTO dto = new ProductDTO();
        dto.setProductId(product.getProductId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setWholesalePrice(product.getWholesalePrice());
        dto.setStock(product.getStock());
        
        if (product.getCategory() != null) {
            dto.setCategoryId(product.getCategory().getCategoryId());
            dto.setCategoryName(product.getCategory().getCategoryName());
        }

        // Map reviews
        if (product.getReviews() != null) {
            List<ReviewDTO> reviewDTOs = product.getReviews().stream()
                .map(reviewMapper::toDTO)
                .collect(Collectors.toList());
            dto.setReviews(reviewDTOs);
        }

        return dto;
    }

    public Product toEntity(ProductDTO dto) {
        if (dto == null) {
            return null;
        }

        Product product = new Product();
        product.setProductId(dto.getProductId());
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setWholesalePrice(dto.getWholesalePrice());
        product.setStock(dto.getStock());

        // Set category if categoryId is provided
        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            product.setCategory(category);
        }

        return product;
    }

    public List<ProductDTO> toDTOList(List<Product> products) {
        if (products == null) {
            return null;
        }
        return products.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
} 