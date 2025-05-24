package com.example.ECommerce.repository;

import com.example.ECommerce.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory_CategoryId(Long categoryId);
}
