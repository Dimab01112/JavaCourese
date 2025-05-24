package com.example.ECommerce.service;

import com.example.ECommerce.entity.Cart;
import com.example.ECommerce.entity.CartItem;
import com.example.ECommerce.entity.Product;
import com.example.ECommerce.entity.KeycloakUser;
import com.example.ECommerce.repository.CartRepository;
import com.example.ECommerce.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private KeycloakUserService keycloakUserService;

    @InjectMocks
    private CartService cartService;

    private Cart testCart;
    private Product testProduct;
    private CartItem testCartItem;
    private KeycloakUser testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = new KeycloakUser();
        testUser.setId("test-user-id");
        testUser.setEmail("test@example.com");
        testUser.setEnabled(true);

        testProduct = new Product();
        testProduct.setProductId(1L);
        testProduct.setName("Test Product");
        testProduct.setPrice(99.99f);
        testProduct.setStock(100);

        testCart = new Cart();
        testCart.setKeycloakUserId(testUser.getId());
        testCart.setCartItems(new ArrayList<>());

        testCartItem = new CartItem();
        testCartItem.setCart(testCart);
        testCartItem.setProduct(testProduct);
        testCartItem.setQuantity(1);
        
        // Add the cart item to the cart
        testCart.getCartItems().add(testCartItem);

        // Mock Keycloak user service
        when(keycloakUserService.getUserFromKeycloak(anyString())).thenReturn(testUser);
    }

    @Test
    void addProductToCart_Success() {
        when(cartRepository.findByKeycloakUserId(anyString())).thenReturn(Optional.of(testCart));
        when(productRepository.findById(any())).thenReturn(Optional.of(testProduct));
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

        Cart updatedCart = cartService.addProductToCart(testUser.getId(), testProduct.getProductId(), 1);

        assertNotNull(updatedCart);
        verify(cartRepository).findByKeycloakUserId(testUser.getId());
        verify(productRepository).findById(testProduct.getProductId());
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    void addProductToCart_ProductNotFound() {
        when(cartRepository.findByKeycloakUserId(anyString())).thenReturn(Optional.of(testCart));
        when(productRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> 
            cartService.addProductToCart(testUser.getId(), testProduct.getProductId(), 1));
        verify(cartRepository).findByKeycloakUserId(testUser.getId());
        verify(productRepository).findById(testProduct.getProductId());
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    void updateProductQuantity_Success() {
        when(cartRepository.findByKeycloakUserId(anyString())).thenReturn(Optional.of(testCart));
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

        Cart updatedCart = cartService.updateProductQuantity(testUser.getId(), testProduct.getProductId(), 2);

        assertNotNull(updatedCart);
        assertEquals(2, updatedCart.getCartItems().get(0).getQuantity());
        verify(cartRepository).findByKeycloakUserId(testUser.getId());
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    void updateProductQuantity_CartNotFound() {
        when(cartRepository.findByKeycloakUserId(anyString())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> 
            cartService.updateProductQuantity(testUser.getId(), testProduct.getProductId(), 2));
        verify(cartRepository).findByKeycloakUserId(testUser.getId());
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    void removeProductFromCart_Success() {
        when(cartRepository.findByKeycloakUserId(anyString())).thenReturn(Optional.of(testCart));
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

        cartService.removeProductFromCart(testUser.getId(), testProduct.getProductId());

        verify(cartRepository).findByKeycloakUserId(testUser.getId());
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    void removeProductFromCart_CartNotFound() {
        when(cartRepository.findByKeycloakUserId(anyString())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> 
            cartService.removeProductFromCart(testUser.getId(), testProduct.getProductId()));
        verify(cartRepository).findByKeycloakUserId(testUser.getId());
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    void getCartByUserId_Success() {
        when(cartRepository.findByKeycloakUserId(anyString())).thenReturn(Optional.of(testCart));

        Cart foundCart = cartService.getCartByUserId(testUser.getId());

        assertNotNull(foundCart);
        assertEquals(testUser.getId(), foundCart.getKeycloakUserId());
        verify(cartRepository).findByKeycloakUserId(testUser.getId());
    }

    @Test
    void getCartByUserId_NotFound() {
        when(cartRepository.findByKeycloakUserId(anyString())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> cartService.getCartByUserId(testUser.getId()));
        verify(cartRepository).findByKeycloakUserId(testUser.getId());
    }
} 