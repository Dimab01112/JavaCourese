package com.example.ECommerce.controller;

import com.example.ECommerce.dto.ProductDTO;
import com.example.ECommerce.entity.Product;
import com.example.ECommerce.mapper.ProductMapper;
import com.example.ECommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController extends BaseController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductMapper productMapper;

    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        List<ProductDTO> productDTOs = productMapper.toDTOList(products);
        
        // Check if user is B2B
        if (!isCurrentUserB2B()) {
            // If not B2B user, remove wholesale prices
            productDTOs.forEach(dto -> dto.setWholesalePrice(null));
        }
        
        return ResponseEntity.ok(productDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }

        ProductDTO productDTO = productMapper.toDTO(product);
        
        // Check if user is B2B
        if (!isCurrentUserB2B()) {
            // If not B2B user, remove wholesale price
            productDTO.setWholesalePrice(null);
        }
        
        return ResponseEntity.ok(productDTO);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductDTO>> getProductsByCategory(@PathVariable Long categoryId) {
        try {
            List<Product> products = productService.getProductsByCategory(categoryId);
            List<ProductDTO> productDTOs = productMapper.toDTOList(products);
            
            // Check if user is B2B
            if (!isCurrentUserB2B()) {
                // If not B2B user, remove wholesale prices
                productDTOs.forEach(dto -> dto.setWholesalePrice(null));
            }
            
            return ResponseEntity.ok(productDTOs);
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Category not found")) {
                return ResponseEntity.notFound().build();
            }
            throw e;
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDTO> createProduct(@RequestBody ProductDTO productDTO) {
        Product product = productMapper.toEntity(productDTO);
        Product savedProduct = productService.saveProduct(product);
        return ResponseEntity.ok(productMapper.toDTO(savedProduct));
    }

    @PutMapping("/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long productId, @RequestBody ProductDTO productDTO) {
        productDTO.setProductId(productId);
        Product product = productMapper.toEntity(productDTO);
        Product updatedProduct = productService.saveProduct(product);
        return ResponseEntity.ok(productMapper.toDTO(updatedProduct));
    }

    @DeleteMapping("/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.ok().build();
    }
} 