package com.example.ECommerce.service;

import com.example.ECommerce.entity.Product;
import com.example.ECommerce.entity.Category;
import com.example.ECommerce.repository.ProductRepository;
import com.example.ECommerce.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public List<Product> getProductsByCategory(Long categoryId) {
        // First check if category exists
        if (!categoryRepository.existsById(categoryId)) {
            throw new RuntimeException("Category not found");
        }
        return productRepository.findByCategory_CategoryId(categoryId);
    }

    public Product getProductById(Long productId) {
        return productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    public void deleteProduct(Long productId) {
        productRepository.deleteById(productId);
    }
} 