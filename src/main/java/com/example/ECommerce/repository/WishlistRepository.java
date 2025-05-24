package com.example.ECommerce.repository;

import com.example.ECommerce.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    List<Wishlist> findByKeycloakUserId(String keycloakUserId);
    boolean existsByKeycloakUserIdAndProduct_ProductId(String keycloakUserId, Long productId);
    void deleteByKeycloakUserIdAndProduct_ProductId(String keycloakUserId, Long productId);
}
