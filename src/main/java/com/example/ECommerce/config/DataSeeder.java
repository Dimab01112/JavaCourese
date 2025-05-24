package com.example.ECommerce.config;

import com.example.ECommerce.entity.Category;
import com.example.ECommerce.entity.Product;
import com.example.ECommerce.repository.CategoryRepository;
import com.example.ECommerce.repository.ProductRepository;
import com.example.ECommerce.service.KeycloakUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private KeycloakUserService keycloakUserService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public void run(String... args) {
        // Create admin user
        if (!keycloakUserService.existsByEmail("admin@example.com")) {
            keycloakUserService.createUser(
                "admin-1",
                "admin@example.com",
                "Admin",
                "User",
                true,
                true,
                false,
                null,
                null
            );
        }

        // Create B2B user
        if (!keycloakUserService.existsByEmail("b2b@example.com")) {
            keycloakUserService.createUser(
                "b2b-1",
                "b2b@example.com",
                "B2B",
                "User",
                true,
                false,
                true,
                "B2B Company",
                "123456789"
            );
        }

        // Create regular user
        if (!keycloakUserService.existsByEmail("user@example.com")) {
            keycloakUserService.createUser(
                "user-1",
                "user@example.com",
                "Regular",
                "User",
                true,
                false,
                false,
                null,
                null
            );
        }

        // Only seed data if the database is empty
        if (categoryRepository.count() == 0) {
            seedCategories();
            seedProducts();
        }
    }

    private void seedCategories() {
        List<Category> categories = Arrays.asList(
            createCategory("Skincare", "Facial care products and treatments"),
            createCategory("Makeup", "Cosmetics and beauty products"),
            createCategory("Haircare", "Hair care products and treatments"),
            createCategory("Fragrances", "Perfumes and scented products"),
            createCategory("Bath & Body", "Body care and bath products")
        );

        categoryRepository.saveAll(categories);
    }

    private Category createCategory(String name, String description) {
        Category category = new Category();
        category.setCategoryName(name);
        return category;
    }

    private void seedProducts() {
        List<Category> categories = categoryRepository.findAll();

        // Skincare products
        Category skincare = categories.stream()
            .filter(c -> c.getCategoryName().equals("Skincare"))
            .findFirst()
            .orElseThrow();

        List<Product> skincareProducts = Arrays.asList(
            createProduct("Hydrating Face Cream", "Moisturizing cream for all skin types", new BigDecimal("29.99"), 100, skincare),
            createProduct("Vitamin C Serum", "Brightening and anti-aging serum", new BigDecimal("39.99"), 75, skincare),
            createProduct("Gentle Cleanser", "Daily facial cleanser", new BigDecimal("19.99"), 150, skincare)
        );

        // Makeup products
        Category makeup = categories.stream()
            .filter(c -> c.getCategoryName().equals("Makeup"))
            .findFirst()
            .orElseThrow();

        List<Product> makeupProducts = Arrays.asList(
            createProduct("Matte Lipstick", "Long-lasting matte finish lipstick", new BigDecimal("24.99"), 200, makeup),
            createProduct("Foundation", "Full coverage foundation", new BigDecimal("34.99"), 150, makeup),
            createProduct("Eyeshadow Palette", "12-color eyeshadow palette", new BigDecimal("49.99"), 100, makeup)
        );

        // Haircare products
        Category haircare = categories.stream()
            .filter(c -> c.getCategoryName().equals("Haircare"))
            .findFirst()
            .orElseThrow();

        List<Product> haircareProducts = Arrays.asList(
            createProduct("Shampoo", "Moisturizing shampoo", new BigDecimal("14.99"), 200, haircare),
            createProduct("Conditioner", "Deep conditioning treatment", new BigDecimal("16.99"), 200, haircare),
            createProduct("Hair Mask", "Repairing hair mask", new BigDecimal("24.99"), 100, haircare)
        );

        // Fragrances
        Category fragrances = categories.stream()
            .filter(c -> c.getCategoryName().equals("Fragrances"))
            .findFirst()
            .orElseThrow();

        List<Product> fragranceProducts = Arrays.asList(
            createProduct("Floral Perfume", "Light floral scent", new BigDecimal("59.99"), 50, fragrances),
            createProduct("Woody Cologne", "Masculine woody fragrance", new BigDecimal("69.99"), 50, fragrances),
            createProduct("Body Mist", "Refreshing body spray", new BigDecimal("19.99"), 150, fragrances)
        );

        // Bath & Body products
        Category bathBody = categories.stream()
            .filter(c -> c.getCategoryName().equals("Bath & Body"))
            .findFirst()
            .orElseThrow();

        List<Product> bathBodyProducts = Arrays.asList(
            createProduct("Body Lotion", "Nourishing body lotion", new BigDecimal("22.99"), 150, bathBody),
            createProduct("Bath Bombs", "Set of 3 scented bath bombs", new BigDecimal("14.99"), 100, bathBody),
            createProduct("Hand Cream", "Moisturizing hand cream", new BigDecimal("12.99"), 200, bathBody)
        );

        productRepository.saveAll(skincareProducts);
        productRepository.saveAll(makeupProducts);
        productRepository.saveAll(haircareProducts);
        productRepository.saveAll(fragranceProducts);
        productRepository.saveAll(bathBodyProducts);
    }

    private Product createProduct(String name, String description, BigDecimal price, int stock, Category category) {
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price.floatValue());
        // Set wholesale price to 70% of retail price
        product.setWholesalePrice(price.multiply(new BigDecimal("0.7")).floatValue());
        product.setStock(stock);
        product.setCategory(category);
        product.setCreatedAt(LocalDateTime.now());
        return product;
    }
} 