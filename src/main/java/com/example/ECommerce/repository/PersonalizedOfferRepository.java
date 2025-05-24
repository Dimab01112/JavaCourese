package com.example.ECommerce.repository;

import com.example.ECommerce.entity.PersonalizedOffer;
import com.example.ECommerce.entity.PersonalizedOffer.OfferStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PersonalizedOfferRepository extends JpaRepository<PersonalizedOffer, Long> {
    List<PersonalizedOffer> findByKeycloakUserId(String keycloakUserId);
    List<PersonalizedOffer> findByStatus(OfferStatus status);
} 