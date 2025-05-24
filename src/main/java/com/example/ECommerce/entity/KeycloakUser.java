package com.example.ECommerce.entity;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.Set;

@Data
public class KeycloakUser {
    private String id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private boolean enabled;
    private Set<String> roles;
    private String companyName;
    private String taxNumber;
    private LocalDateTime createdAt;
} 