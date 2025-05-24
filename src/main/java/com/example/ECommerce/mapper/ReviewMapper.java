package com.example.ECommerce.mapper;

import com.example.ECommerce.entity.KeycloakUser;
import com.example.ECommerce.dto.ReviewDTO;
import com.example.ECommerce.entity.Review;
import com.example.ECommerce.service.KeycloakUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ReviewMapper {
    
    @Autowired
    private KeycloakUserService keycloakUserService;
    
    public ReviewDTO toDTO(Review review) {
        if (review == null) {
            return null;
        }
        
        ReviewDTO dto = new ReviewDTO();
        dto.setId(review.getId());
        dto.setKeycloakUserId(review.getKeycloakUserId());
        dto.setProductId(review.getProduct().getProductId());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setCreatedAt(review.getCreatedAt());

        KeycloakUser user = keycloakUserService.getUserFromKeycloak(review.getKeycloakUserId());
        if (user != null) {
            dto.setUserName(user.getFirstName() + " " + user.getLastName());
        }

        return dto;
    }

    public Review toEntity(ReviewDTO dto) {
        if (dto == null) {
            return null;
        }

        Review review = new Review();
        review.setId(dto.getId());
        review.setKeycloakUserId(dto.getKeycloakUserId());
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());
        review.setCreatedAt(dto.getCreatedAt());
        return review;
    }

    public List<ReviewDTO> toDTOList(List<Review> reviews) {
        if (reviews == null) {
            return null;
        }
        return reviews.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
} 