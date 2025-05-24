package com.example.ECommerce.service;

import com.example.ECommerce.dto.WishlistItemDTO;
import com.example.ECommerce.entity.Product;
import com.example.ECommerce.entity.Wishlist;
import com.example.ECommerce.entity.KeycloakUser;
import com.example.ECommerce.repository.ProductRepository;
import com.example.ECommerce.repository.WishlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class WishlistService {
    
    private final WishlistRepository wishlistRepository;
    private final KeycloakUserService keycloakUserService;
    private final ProductRepository productRepository;
    
    @Autowired
    public WishlistService(WishlistRepository wishlistRepository,
                          KeycloakUserService keycloakUserService,
                          ProductRepository productRepository) {
        this.wishlistRepository = wishlistRepository;
        this.keycloakUserService = keycloakUserService;
        this.productRepository = productRepository;
    }
    
    public Wishlist addToWishlist(WishlistItemDTO itemDTO) {
        // Validate user
        KeycloakUser user = keycloakUserService.getUserFromKeycloak(itemDTO.getKeycloakUserId());
        if (!user.isEnabled()) {
            throw new RuntimeException("User account is disabled");
        }

        // Validate product
        Product product = productRepository.findById(itemDTO.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        // Check if item already exists in wishlist
        if (wishlistRepository.existsByKeycloakUserIdAndProduct_ProductId(
                itemDTO.getKeycloakUserId(), itemDTO.getProductId())) {
            throw new RuntimeException("Product already in wishlist");
        }
        
        Wishlist wishlist = new Wishlist();
        wishlist.setKeycloakUserId(itemDTO.getKeycloakUserId());
        wishlist.setProduct(product);
        wishlist.setCreatedAt(LocalDateTime.now());
        return wishlistRepository.save(wishlist);
    }
    
    public List<Wishlist> getUserWishlist(String keycloakUserId) {
        // Validate user
        KeycloakUser user = keycloakUserService.getUserFromKeycloak(keycloakUserId);
        if (!user.isEnabled()) {
            throw new RuntimeException("User account is disabled");
        }
        return wishlistRepository.findByKeycloakUserId(keycloakUserId);
    }
    
    public void removeFromWishlist(String keycloakUserId, Long productId) {
        // Validate user
        KeycloakUser user = keycloakUserService.getUserFromKeycloak(keycloakUserId);
        if (!user.isEnabled()) {
            throw new RuntimeException("User account is disabled");
        }
        wishlistRepository.deleteByKeycloakUserIdAndProduct_ProductId(keycloakUserId, productId);
    }
} 