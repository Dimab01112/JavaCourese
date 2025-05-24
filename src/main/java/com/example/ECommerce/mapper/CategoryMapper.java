package com.example.ECommerce.mapper;

import com.example.ECommerce.dto.CategoryDTO;
import com.example.ECommerce.entity.Category;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CategoryMapper {
    
    public CategoryDTO toDTO(Category category) {
        if (category == null) {
            return null;
        }
        
        CategoryDTO dto = new CategoryDTO();
        dto.setCategoryId(category.getCategoryId());
        dto.setCategoryName(category.getCategoryName());
        return dto;
    }

    public Category toEntity(CategoryDTO dto) {
        if (dto == null) {
            return null;
        }

        Category category = new Category();
        category.setCategoryId(dto.getCategoryId());
        category.setCategoryName(dto.getCategoryName());
        return category;
    }

    public List<CategoryDTO> toDTOList(List<Category> categories) {
        if (categories == null) {
            return null;
        }
        return categories.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
} 