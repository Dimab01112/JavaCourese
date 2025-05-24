package com.example.ECommerce.util;

import com.example.ECommerce.entity.KeycloakUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashSet;

public class KeycloakUtil {
    
    public static KeycloakUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Jwt) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            
            KeycloakUser user = new KeycloakUser();
            user.setId(jwt.getSubject());
            user.setUsername(jwt.getClaimAsString("preferred_username"));
            user.setEmail(jwt.getClaimAsString("email"));
            user.setFirstName(jwt.getClaimAsString("given_name"));
            user.setLastName(jwt.getClaimAsString("family_name"));
            
            // Extract roles from JWT
            Map<String, Object> realmAccess = (Map<String, Object>) jwt.getClaims().get("realm_access");
            if (realmAccess != null && realmAccess.get("roles") != null) {
                @SuppressWarnings("unchecked")
                java.util.List<String> roles = (java.util.List<String>) realmAccess.get("roles");
                user.setRoles(new HashSet<>(roles));
            }
            
            // Set default values
            user.setEnabled(true);
            user.setCreatedAt(LocalDateTime.now());
            
            return user;
        }
        throw new RuntimeException("User not authenticated");
    }

    public static String getTokenString() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Jwt) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            return jwt.getTokenValue();
        }
        throw new RuntimeException("User not authenticated");
    }

    public static boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Jwt) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            Map<String, Object> realmAccess = (Map<String, Object>) jwt.getClaims().get("realm_access");
            if (realmAccess != null && realmAccess.get("roles") != null) {
                @SuppressWarnings("unchecked")
                java.util.List<String> roles = (java.util.List<String>) realmAccess.get("roles");
                return roles.contains(role);
            }
        }
        return false;
    }

    public static boolean isCurrentUserAdmin() {
        return hasRole("ADMIN");
    }

    public static boolean isCurrentUserB2B() {
        return hasRole("B2B");
    }
} 