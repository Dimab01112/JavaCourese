package com.example.ECommerce.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "products")
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    private String name;
    private String description;

    @Column(columnDefinition = "FLOAT")
    private Float price;

    private Float wholesalePrice;
    private Integer stock;
    private String imageUrl;
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    // getters and setters
}
