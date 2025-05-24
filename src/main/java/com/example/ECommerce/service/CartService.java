package com.example.ECommerce.service;

import com.example.ECommerce.entity.Cart;
import com.example.ECommerce.entity.CartItem;
import com.example.ECommerce.entity.Product;
import com.example.ECommerce.entity.KeycloakUser;
import com.example.ECommerce.repository.CartRepository;
import com.example.ECommerce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private KeycloakUserService keycloakUserService;

    public Cart addProductToCart(String userId, Long productId, int quantity) {
        // Validate user
        KeycloakUser user = keycloakUserService.getUserFromKeycloak(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // Get or create cart for user
        Cart cart = cartRepository.findByKeycloakUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setKeycloakUserId(userId);
                    newCart.setCreatedAt(LocalDateTime.now());
                    newCart.setUpdatedAt(LocalDateTime.now());
                    return cartRepository.save(newCart);
                });

        // Get product
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Check if product is already in cart
        Optional<CartItem> existingItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getProductId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            // Update quantity if product already exists in cart
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
        } else {
            // Create new cart item if product doesn't exist in cart
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            cart.getCartItems().add(newItem);
        }

        cart.setUpdatedAt(LocalDateTime.now());
        return cartRepository.save(cart);
    }

    public Cart updateProductQuantity(String userId, Long productId, int quantity) {
        // Validate user
        KeycloakUser user = keycloakUserService.getUserFromKeycloak(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }


        Cart cart = cartRepository.findByKeycloakUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        CartItem cartItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Product not found in cart"));

        cartItem.setQuantity(quantity);
        return cartRepository.save(cart);
    }

    public void removeProductFromCart(String userId, Long productId) {
        // Validate user
        KeycloakUser user = keycloakUserService.getUserFromKeycloak(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }


        Cart cart = cartRepository.findByKeycloakUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        cart.getCartItems().removeIf(item -> item.getProduct().getProductId().equals(productId));
        cartRepository.save(cart);
    }

    public Cart getCartByUserId(String userId) {
        // Validate user
        KeycloakUser user = keycloakUserService.getUserFromKeycloak(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }


        return cartRepository.findByKeycloakUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
    }
} 