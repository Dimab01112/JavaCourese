package com.example.ECommerce.service;

import com.example.ECommerce.dto.CategoryDTO;
import com.example.ECommerce.dto.CreateCategoryDTO;
import com.example.ECommerce.dto.UpdateCategoryDTO;
import com.example.ECommerce.entity.Category;
import com.example.ECommerce.repository.CategoryRepository;
import com.example.ECommerce.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional
    public CategoryDTO createCategory(CreateCategoryDTO dto) {
        Category category = new Category();
        category.setCategoryName(dto.getCategoryName());
        category.setDescription(dto.getDescription());
        return convertToDTO(categoryRepository.save(category));
    }

    @Transactional
    public CategoryDTO updateCategory(Long categoryId, UpdateCategoryDTO dto) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));

        category.setCategoryName(dto.getCategoryName());
        category.setDescription(dto.getDescription());

        return convertToDTO(categoryRepository.save(category));
    }

    @Transactional
    public void deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));

        categoryRepository.delete(category);
    }

    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public CategoryDTO getCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .map(this::convertToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
    }

    private CategoryDTO convertToDTO(Category category) {
        CategoryDTO dto = new CategoryDTO();
        dto.setCategoryId(category.getCategoryId());
        dto.setCategoryName(category.getCategoryName());
        dto.setDescription(category.getDescription());
        dto.setCreatedAt(category.getCreatedAt());
        dto.setUpdatedAt(category.getUpdatedAt());
        dto.setProductCount(category.getProducts() != null ? category.getProducts().size() : 0);
        return dto;
    }
} 