package com.example.ECommerce.service;

import com.example.ECommerce.entity.KeycloakUser;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

@Service
public class KeycloakUserService {
    private static final Logger logger = LoggerFactory.getLogger(KeycloakUserService.class);

    @Autowired
    private Keycloak keycloak;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.auth-server-url}")
    private String authServerUrl;

    @Value("${keycloak.resource}")
    private String clientId;

    @Value("${keycloak.credentials.secret}")
    private String clientSecret;

    public KeycloakUserService(@Value("${keycloak.realm}") String realm) {
        this.realm = realm;
    }

    @Transactional
    public KeycloakUser getUserFromKeycloak(String userId) {
        try {
            UserRepresentation user = keycloak.realm(realm).users().get(userId).toRepresentation();
            if (user == null) {
                logger.warn("User not found in Keycloak: {}", userId);
                return null;
            }
            return convertToKeycloakUser(user);
        } catch (Exception e) {
            logger.error("Error fetching user from Keycloak: {}", e.getMessage());
            return null;
        }
    }

    @Transactional
    public List<KeycloakUser> getAllUsers() {
        try {
            List<UserRepresentation> users = keycloak.realm(realm).users().list();
            return users.stream()
                    .map(this::convertToKeycloakUser)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error fetching all users from Keycloak: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch users from Keycloak", e);
        }
    }

    private KeycloakUser convertToKeycloakUser(UserRepresentation userRep) {
        KeycloakUser user = new KeycloakUser();
        user.setId(userRep.getId());
        user.setEmail(userRep.getEmail());
        user.setUsername(userRep.getUsername());
        user.setFirstName(userRep.getFirstName());
        user.setLastName(userRep.getLastName());
        user.setEnabled(userRep.isEnabled());

        // Set roles
        Set<String> roles = new HashSet<>();
        if (userRep.getRealmRoles() != null) {
            roles.addAll(userRep.getRealmRoles());
        }
        user.setRoles(roles);

        // Handle B2B attributes
        Map<String, List<String>> attributes = userRep.getAttributes();
        if (attributes != null) {
            user.setCompanyName(getStringAttribute(attributes, "companyName"));
            user.setTaxNumber(getStringAttribute(attributes, "taxNumber"));
        }

        return user;
    }

    private String getStringAttribute(Map<String, List<String>> attributes, String key) {
        if (attributes != null && attributes.containsKey(key)) {
            List<String> values = attributes.get(key);
            return values != null && !values.isEmpty() ? values.get(0) : null;
        }
        return null;
    }

    public Optional<UserRepresentation> getUserById(String userId) {
        try {
            UserRepresentation user = keycloak.realm(realm).users().get(userId).toRepresentation();
            return Optional.of(user);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<UserRepresentation> getUserByUsername(String username) {
        try {
            List<UserRepresentation> users = keycloak.realm(realm).users().search(username);
            return users.stream().findFirst();
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<UserRepresentation> getUserByEmail(String email) {
        try {
            List<UserRepresentation> users = keycloak.realm(realm).users().searchByEmail(email, true);
            return users.stream().findFirst();
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public boolean isUserActive(String userId) {
        return getUserById(userId)
                .map(UserRepresentation::isEnabled)
                .orElse(false);
    }

    public boolean existsByEmail(String email) {
        try {
            UsersResource users = keycloak.realm(realm).users();
            List<UserRepresentation> usersList = users.search(email);
            return !usersList.isEmpty();
        } catch (Exception e) {
            logger.error("Error checking if user exists by email: {}", e.getMessage());
            // If we can't check, assume the user doesn't exist
            return false;
        }
    }

    public void createUser(String id, String email, String firstName, String lastName,
                          boolean enabled, boolean admin, boolean b2b,
                          String companyName, String taxNumber) {
        UsersResource users = keycloak.realm(realm).users();
        
        UserRepresentation user = new UserRepresentation();
        user.setId(id);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEnabled(enabled);
        user.setEmailVerified(true);
        
        // Set attributes
        user.singleAttribute("admin", String.valueOf(admin));
        user.singleAttribute("b2b", String.valueOf(b2b));
        if (companyName != null) {
            user.singleAttribute("companyName", companyName);
        }
        if (taxNumber != null) {
            user.singleAttribute("taxNumber", taxNumber);
        }
        
        users.create(user);
    }

    public UserRepresentation getUserRepresentationById(String userId) {
        try {
            UsersResource users = keycloak.realm(realm).users();
            return users.get(userId).toRepresentation();
        } catch (Exception e) {
            logger.error("Error getting user from Keycloak: {}", e.getMessage());
            return null;
        }
    }

    public boolean isEnabled(String userId) {
        UserRepresentation user = getUserRepresentationById(userId);
        return user != null && user.isEnabled();
    }
} 