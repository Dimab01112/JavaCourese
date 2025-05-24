package com.example.ECommerce.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class WishlistDTO {
    private Long id;
    private String keycloakUserId;
    private String userName;
    private List<WishlistItemDTO> items;
    private LocalDateTime createdAt;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKeycloakUserId() {
        return keycloakUserId;
    }

    public void setKeycloakUserId(String keycloakUserId) {
        this.keycloakUserId = keycloakUserId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<WishlistItemDTO> getItems() {
        return items;
    }

    public void setItems(List<WishlistItemDTO> items) {
        this.items = items;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
} 