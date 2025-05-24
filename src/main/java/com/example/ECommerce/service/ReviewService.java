package com.example.ECommerce.service;

import com.example.ECommerce.entity.KeycloakUser;
import com.example.ECommerce.dto.ReviewDTO;
import com.example.ECommerce.entity.Review;
import com.example.ECommerce.entity.Product;
import com.example.ECommerce.mapper.ReviewMapper;
import com.example.ECommerce.repository.ProductRepository;
import com.example.ECommerce.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final KeycloakUserService keycloakUserService;
    private final ReviewMapper reviewMapper;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository, 
                        ProductRepository productRepository,
                        KeycloakUserService keycloakUserService,
                        ReviewMapper reviewMapper) {
        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
        this.keycloakUserService = keycloakUserService;
        this.reviewMapper = reviewMapper;
    }

    public List<ReviewDTO> getReviewsByUser(String userId) {
        KeycloakUser user = keycloakUserService.getUserFromKeycloak(userId);
        List<Review> reviews = reviewRepository.findByKeycloakUserId(userId);
        return reviewMapper.toDTOList(reviews);
    }

    public ReviewDTO createReview(ReviewDTO reviewDTO) {
        // Validate user
        KeycloakUser user = keycloakUserService.getUserFromKeycloak(reviewDTO.getKeycloakUserId());
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // Validate product
        Product product = productRepository.findById(reviewDTO.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Validate rating
        if (reviewDTO.getRating() < 1 || reviewDTO.getRating() > 5) {
            throw new IllegalArgumentException("Invalid rating");
        }

        // Validate comment
        if (reviewDTO.getComment() == null || reviewDTO.getComment().trim().isEmpty()) {
            throw new IllegalArgumentException("Comment cannot be empty");
        }

        // Create review
        Review review = reviewMapper.toEntity(reviewDTO);
        review.setCreatedAt(LocalDateTime.now());
        review.setProduct(product);

        // Save review
        Review savedReview = reviewRepository.save(review);
        return reviewMapper.toDTO(savedReview);
    }

    public void deleteReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        reviewRepository.delete(review);
    }

    public List<ReviewDTO> getReviewsByProduct(Long productId) {
        List<Review> reviews = reviewRepository.findByProduct_ProductId(productId);
        return reviewMapper.toDTOList(reviews);
    }

    @Transactional
    public List<ReviewDTO> getUserReviews(String userId) {
        KeycloakUser user = keycloakUserService.getUserFromKeycloak(userId);
        List<Review> reviews = reviewRepository.findByKeycloakUserId(userId);
        return reviewMapper.toDTOList(reviews);
    }

    @Transactional
    public ReviewDTO updateReview(Long reviewId, String userId, ReviewDTO reviewDTO) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        // Check if the user is the owner of the review
        if (!review.getKeycloakUserId().equals(userId)) {
            throw new RuntimeException("User is not authorized to update this review");
        }

        // Validate rating
        if (reviewDTO.getRating() < 1 || reviewDTO.getRating() > 5) {
            throw new RuntimeException("Invalid rating");
        }

        // Validate comment
        if (reviewDTO.getComment() == null || reviewDTO.getComment().trim().isEmpty()) {
            throw new RuntimeException("Comment cannot be empty");
        }

        // Update review
        review.setRating(reviewDTO.getRating());
        review.setComment(reviewDTO.getComment());

        Review updatedReview = reviewRepository.save(review);
        return reviewMapper.toDTO(updatedReview);
    }
} 