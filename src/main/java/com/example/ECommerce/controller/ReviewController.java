package com.example.ECommerce.controller;

import com.example.ECommerce.dto.ReviewDTO;
import com.example.ECommerce.dto.ReviewCreateDTO;
import com.example.ECommerce.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController extends BaseController {

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ReviewDTO>> getProductReviews(@PathVariable Long productId) {
        try {
            return ResponseEntity.ok(reviewService.getReviewsByProduct(productId));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Product not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/product/{productId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReviewDTO> createReview(
            @PathVariable Long productId,
            @RequestBody ReviewCreateDTO reviewCreateDTO) {
        try {
            String userId = getCurrentUserId();
            if (userId == null) {
                return ResponseEntity.badRequest().build();
            }
            ReviewDTO reviewDTO = new ReviewDTO();
            reviewDTO.setKeycloakUserId(userId);
            reviewDTO.setProductId(productId);
            reviewDTO.setRating(reviewCreateDTO.getRating());
            reviewDTO.setComment(reviewCreateDTO.getComment());
            return ResponseEntity.ok(reviewService.createReview(reviewDTO));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Product not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{reviewId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReviewDTO> updateReview(
            @PathVariable Long reviewId,
            @RequestBody ReviewDTO reviewDTO) {
        try {
            String userId = getCurrentUserId();
            if (userId == null) {
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.ok(reviewService.updateReview(reviewId, userId, reviewDTO));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Review not found")) {
                return ResponseEntity.notFound().build();
            }
            if (e.getMessage().equals("Not authorized to update this review")) {
                return ResponseEntity.status(403).build();
            }
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{reviewId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId) {
        try {
            String userId = getCurrentUserId();
            if (userId == null) {
                return ResponseEntity.badRequest().build();
            }
            reviewService.deleteReview(reviewId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Review not found")) {
                return ResponseEntity.notFound().build();
            }
            if (e.getMessage().equals("Not authorized to delete this review")) {
                return ResponseEntity.status(403).build();
            }
            return ResponseEntity.badRequest().build();
        }
    }
} 