package com.example.ECommerce.service;

import com.example.ECommerce.dto.CategoryDTO;
import com.example.ECommerce.dto.CreateCategoryDTO;
import com.example.ECommerce.dto.UpdateCategoryDTO;
import com.example.ECommerce.entity.Category;
import com.example.ECommerce.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category testCategory;
    private CreateCategoryDTO createCategoryDTO;
    private UpdateCategoryDTO updateCategoryDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testCategory = new Category();
        testCategory.setCategoryId(1L);
        testCategory.setCategoryName("Test Category");
        testCategory.setDescription("Test Description");

        createCategoryDTO = new CreateCategoryDTO();
        createCategoryDTO.setCategoryName("Test Category");
        createCategoryDTO.setDescription("Test Description");

        updateCategoryDTO = new UpdateCategoryDTO();
        updateCategoryDTO.setCategoryName("Updated Category");
        updateCategoryDTO.setDescription("Updated Description");
    }

    @Test
    void createCategory_Success() {
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);

        CategoryDTO savedCategory = categoryService.createCategory(createCategoryDTO);

        assertNotNull(savedCategory);
        assertEquals(testCategory.getCategoryName(), savedCategory.getCategoryName());
        assertEquals(testCategory.getDescription(), savedCategory.getDescription());
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void updateCategory_Success() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);

        CategoryDTO updatedCategory = categoryService.updateCategory(1L, updateCategoryDTO);

        assertNotNull(updatedCategory);
        assertEquals(testCategory.getCategoryName(), updatedCategory.getCategoryName());
        assertEquals(testCategory.getDescription(), updatedCategory.getDescription());
        verify(categoryRepository).findById(1L);
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void updateCategory_NotFound() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> categoryService.updateCategory(1L, updateCategoryDTO));
        verify(categoryRepository).findById(1L);
    }

    @Test
    void getAllCategories_Success() {
        List<Category> categories = Arrays.asList(testCategory);
        when(categoryRepository.findAll()).thenReturn(categories);

        List<CategoryDTO> foundCategories = categoryService.getAllCategories();

        assertNotNull(foundCategories);
        assertEquals(1, foundCategories.size());
        assertEquals(testCategory.getCategoryName(), foundCategories.get(0).getCategoryName());
        verify(categoryRepository).findAll();
    }

    @Test
    void deleteCategory_Success() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        doNothing().when(categoryRepository).delete(any(Category.class));

        categoryService.deleteCategory(1L);

        verify(categoryRepository).findById(1L);
        verify(categoryRepository).delete(any(Category.class));
    }

    @Test
    void deleteCategory_NotFound() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> categoryService.deleteCategory(1L));
        verify(categoryRepository).findById(1L);
    }
} 