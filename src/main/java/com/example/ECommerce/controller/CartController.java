package com.example.ECommerce.controller;

import com.example.ECommerce.dto.CartDTO;
import com.example.ECommerce.dto.CartItemDTO;
import com.example.ECommerce.dto.AddToCartDTO;
import com.example.ECommerce.entity.Cart;
import com.example.ECommerce.mapper.CartMapper;
import com.example.ECommerce.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@PreAuthorize("isAuthenticated()")
public class CartController extends BaseController {

    @Autowired
    private CartService cartService;

    @Autowired
    private CartMapper cartMapper;

    @GetMapping
    public ResponseEntity<CartDTO> getCurrentUserCart() {
        String userId = getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.badRequest().build();
        }
        try {
            Cart cart = cartService.getCartByUserId(userId);
            return ResponseEntity.ok(cartMapper.toDTO(cart));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Cart not found")) {
                return ResponseEntity.notFound().build();
            }
            throw e;
        }
    }

    @PostMapping("/items")
    public ResponseEntity<CartDTO> addItemToCart(@RequestBody AddToCartDTO addToCartDTO) {
        String userId = getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.badRequest().build();
        }
        try {
            Cart cart = cartService.addProductToCart(userId, addToCartDTO.getProductId(), addToCartDTO.getQuantity());
            return ResponseEntity.ok(cartMapper.toDTO(cart));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Product not found") ||
                    e.getMessage().equals("Invalid quantity") ||
                    e.getMessage().equals("Cart not found")) {
                return ResponseEntity.badRequest().build();
            }
            throw e;
        }
    }

    @PutMapping("/items/{productId}")
    public ResponseEntity<CartDTO> updateCartItem(
            @PathVariable Long productId,
            @RequestBody CartItemDTO cartItemDTO) {
        String userId = getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.badRequest().build();
        }
        try {
            Cart cart = cartService.updateProductQuantity(userId, productId, cartItemDTO.getQuantity());
            return ResponseEntity.ok(cartMapper.toDTO(cart));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Cart item not found") ||
                    e.getMessage().equals("Invalid quantity") ||
                    e.getMessage().equals("Cart not found")) {
                return ResponseEntity.badRequest().build();
            }
            throw e;
        }
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<CartDTO> removeItemFromCart(@PathVariable Long productId) {
        String userId = getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.badRequest().build();
        }
        try {
            cartService.removeProductFromCart(userId, productId);
            Cart cart = cartService.getCartByUserId(userId);
            return ResponseEntity.ok(cartMapper.toDTO(cart));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Cart item not found") ||
                    e.getMessage().equals("Cart not found")) {
                return ResponseEntity.badRequest().build();
            }
            throw e;
        }
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart() {
        String userId = getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.badRequest().build();
        }
        try {
            Cart cart = cartService.getCartByUserId(userId);
            cart.getCartItems().clear();
            cartService.updateProductQuantity(userId, null, 0);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Cart not found")) {
                return ResponseEntity.notFound().build();
            }
            throw e;
        }
    }
} 