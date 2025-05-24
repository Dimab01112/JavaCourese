package com.example.ECommerce.controller;

import com.example.ECommerce.dto.CategoryDTO;
import com.example.ECommerce.dto.CreateCategoryDTO;
import com.example.ECommerce.dto.UpdateCategoryDTO;
import com.example.ECommerce.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController extends BaseController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryDTO> createCategory(@RequestBody CreateCategoryDTO dto) {
        try {
            return ResponseEntity.ok(categoryService.createCategory(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryDTO> updateCategory(
            @PathVariable Long categoryId,
            @RequestBody UpdateCategoryDTO dto) {
        try {
            return ResponseEntity.ok(categoryService.updateCategory(categoryId, dto));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Category not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long categoryId) {
        try {
            categoryService.deleteCategory(categoryId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Category not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        try {
            return ResponseEntity.ok(categoryService.getAllCategories());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryDTO> getCategory(@PathVariable Long categoryId) {
        try {
            return ResponseEntity.ok(categoryService.getCategory(categoryId));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Category not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().build();
        }
    }
} 