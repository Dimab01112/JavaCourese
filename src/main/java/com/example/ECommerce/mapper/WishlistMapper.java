package com.example.ECommerce.mapper;

import com.example.ECommerce.dto.WishlistDTO;
import com.example.ECommerce.dto.WishlistItemDTO;
import com.example.ECommerce.entity.Wishlist;
import com.example.ECommerce.entity.KeycloakUser;
import com.example.ECommerce.service.KeycloakUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;
import java.math.BigDecimal;

@Component
public class WishlistMapper {
    
    @Autowired
    private KeycloakUserService keycloakUserService;
    
    public WishlistDTO toDTO(Wishlist wishlist) {
        if (wishlist == null) {
            return null;
        }

        WishlistDTO dto = new WishlistDTO();
        dto.setId(wishlist.getId());
        dto.setKeycloakUserId(wishlist.getKeycloakUserId());
        dto.setCreatedAt(wishlist.getCreatedAt());

        KeycloakUser user = keycloakUserService.getUserFromKeycloak(wishlist.getKeycloakUserId());
        if (user != null) {
            dto.setUserName(user.getFirstName() + " " + user.getLastName());
        }

        return dto;
    }

    public WishlistItemDTO toItemDTO(Wishlist wishlist) {
        if (wishlist == null) {
            return null;
        }

        WishlistItemDTO dto = new WishlistItemDTO();
        dto.setWishlistItemId(wishlist.getId());
        dto.setKeycloakUserId(wishlist.getKeycloakUserId());
        dto.setProductId(wishlist.getProduct().getProductId());
        dto.setProductName(wishlist.getProduct().getName());
        dto.setPrice(wishlist.getProduct().getPrice());
        dto.setImageUrl(wishlist.getProduct().getImageUrl());
        return dto;
    }

    public Wishlist toEntity(WishlistDTO dto) {
        if (dto == null) {
            return null;
        }

        Wishlist wishlist = new Wishlist();
        wishlist.setId(dto.getId());
        wishlist.setKeycloakUserId(dto.getKeycloakUserId());
        wishlist.setCreatedAt(dto.getCreatedAt());
        return wishlist;
    }
} 