package com.example.ECommerce.mapper;

import com.example.ECommerce.dto.CartItemDTO;
import com.example.ECommerce.entity.CartItem;
import org.springframework.stereotype.Component;

@Component
public class CartItemMapper {
    
    public CartItemDTO toDTO(CartItem cartItem) {
        if (cartItem == null) {
            return null;
        }
        
        CartItemDTO dto = new CartItemDTO();
        dto.setId(cartItem.getId());
        dto.setProductId(cartItem.getProduct().getProductId());
        dto.setProductName(cartItem.getProduct().getName());
        dto.setQuantity(cartItem.getQuantity());
        dto.setPrice(cartItem.getProduct().getPrice());
        dto.setSubtotal(cartItem.getProduct().getPrice() * cartItem.getQuantity());
        dto.setImageUrl(cartItem.getProduct().getImageUrl());
        return dto;
    }
    
    public CartItem toEntity(CartItemDTO dto) {
        if (dto == null) {
            return null;
        }
        
        CartItem cartItem = new CartItem();
        cartItem.setId(dto.getId());
        cartItem.setQuantity(dto.getQuantity());
        return cartItem;
    }
} 