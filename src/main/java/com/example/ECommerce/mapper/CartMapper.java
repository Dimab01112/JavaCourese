package com.example.ECommerce.mapper;

import com.example.ECommerce.dto.CartDTO;
import com.example.ECommerce.dto.CartItemDTO;
import com.example.ECommerce.entity.Cart;
import com.example.ECommerce.entity.CartItem;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CartMapper {

    public CartDTO toDTO(Cart cart) {
        if (cart == null) {
            return null;
        }

        CartDTO dto = new CartDTO();
        dto.setId(cart.getId());
        dto.setKeycloakUserId(cart.getKeycloakUserId());
        dto.setItems(cart.getCartItems().stream()
                .map(this::toItemDTO)
                .collect(Collectors.toList()));
        dto.setTotalItems(cart.getCartItems().size());
        dto.setTotalPrice(calculateTotalPrice(cart));
        dto.setCreatedAt(cart.getCreatedAt());

        return dto;
    }

    public CartItemDTO toItemDTO(CartItem cartItem) {
        if (cartItem == null) {
            return null;
        }

        CartItemDTO dto = new CartItemDTO();
        dto.setId(cartItem.getId());
        dto.setProductId(cartItem.getProduct().getProductId());
        dto.setProductName(cartItem.getProduct().getName());
        dto.setQuantity(cartItem.getQuantity());
        dto.setPrice(cartItem.getProduct().getPrice());
        dto.setImageUrl(cartItem.getProduct().getImageUrl());

        return dto;
    }

    private float calculateTotalPrice(Cart cart) {
        return cart.getCartItems().stream()
                .map(item -> item.getProduct().getPrice() * item.getQuantity())
                .reduce(0.0f, Float::sum);
    }

    public List<CartDTO> toDTOList(List<Cart> carts) {
        if (carts == null) {
            return null;
        }
        return carts.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
} 