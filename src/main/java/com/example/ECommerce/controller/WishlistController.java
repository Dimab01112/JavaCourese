package com.example.ECommerce.controller;

import com.example.ECommerce.dto.WishlistDTO;
import com.example.ECommerce.dto.WishlistItemDTO;
import com.example.ECommerce.service.WishlistService;
import com.example.ECommerce.mapper.WishlistMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
@PreAuthorize("isAuthenticated()")
public class WishlistController extends BaseController {

    @Autowired
    private WishlistService wishlistService;

    @Autowired
    private WishlistMapper wishlistMapper;

    @GetMapping
    public ResponseEntity<List<WishlistItemDTO>> getCurrentUserWishlist() {
        String userId = getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(wishlistService.getUserWishlist(userId).stream()
                .map(wishlistMapper::toItemDTO)
                .toList());
    }

    @PostMapping("/products/{productId}")
    public ResponseEntity<WishlistItemDTO> addProductToWishlist(@PathVariable Long productId) {
        String userId = getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.badRequest().build();
        }
        WishlistItemDTO itemDTO = new WishlistItemDTO();
        itemDTO.setKeycloakUserId(userId);
        itemDTO.setProductId(productId);
        return ResponseEntity.ok(wishlistMapper.toItemDTO(wishlistService.addToWishlist(itemDTO)));
    }

    @DeleteMapping("/products/{productId}")
    public ResponseEntity<List<WishlistItemDTO>> removeProductFromWishlist(@PathVariable Long productId) {
        String userId = getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.badRequest().build();
        }
        wishlistService.removeFromWishlist(userId, productId);
        return ResponseEntity.ok(wishlistService.getUserWishlist(userId).stream()
                .map(wishlistMapper::toItemDTO)
                .toList());
    }

    @DeleteMapping
    public ResponseEntity<Void> clearWishlist() {
        String userId = getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.badRequest().build();
        }
        wishlistService.removeFromWishlist(userId, null);
        return ResponseEntity.ok().build();
    }
} 