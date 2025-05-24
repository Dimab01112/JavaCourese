package com.example.ECommerce.service;

import com.example.ECommerce.dto.CreatePersonalizedOfferDTO;
import com.example.ECommerce.dto.PersonalizedOfferDTO;
import com.example.ECommerce.dto.UpdatePersonalizedOfferDTO;
import com.example.ECommerce.entity.PersonalizedOffer;
import com.example.ECommerce.entity.PersonalizedOffer.OfferStatus;
import com.example.ECommerce.repository.PersonalizedOfferRepository;
import com.example.ECommerce.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PersonalizedOfferService {

    @Autowired
    private PersonalizedOfferRepository offerRepository;

    @Autowired
    private KeycloakUserService keycloakUserService;

    @Transactional
    public PersonalizedOfferDTO createOffer(String userId, CreatePersonalizedOfferDTO dto) {
        // Validate user and check B2B status
        var user = keycloakUserService.getUserFromKeycloak(userId);
        if (!user.isEnabled()) {
            throw new IllegalArgumentException("User account is disabled");
        }

        PersonalizedOffer offer = new PersonalizedOffer();
        offer.setKeycloakUserId(userId);
        offer.setProductName(dto.getProductName());
        offer.setQuantity(dto.getQuantity());
        offer.setDeliveryTerms(dto.getDeliveryTerms());
        offer.setPaymentTerms(dto.getPaymentTerms());
        offer.setAdditionalNotes(dto.getAdditionalNotes());
        offer.setStatus(OfferStatus.PENDING);
        offer.setProposedPrice(0.0f);  // Initialize proposedPrice to 0.0f

        return convertToDTO(offerRepository.save(offer));
    }

    @Transactional
    public PersonalizedOfferDTO updateOffer(Long offerId, UpdatePersonalizedOfferDTO dto) {
        PersonalizedOffer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new ResourceNotFoundException("Offer not found"));

        offer.setStatus(OfferStatus.valueOf(dto.getStatus()));
        offer.setManagerResponse(dto.getManagerResponse());
        offer.setProposedPrice(dto.getProposedPrice().floatValue());

        return convertToDTO(offerRepository.save(offer));
    }

    public List<PersonalizedOfferDTO> getUserOffers(String userId) {
        // Validate user
        var user = keycloakUserService.getUserFromKeycloak(userId);
        if (!user.isEnabled()) {
            throw new IllegalArgumentException("User account is disabled");
        }

        return offerRepository.findByKeycloakUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<PersonalizedOfferDTO> getPendingOffers() {
        return offerRepository.findByStatus(OfferStatus.PENDING).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteOffer(Long offerId) {
        PersonalizedOffer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new ResourceNotFoundException("Offer not found"));
        offerRepository.delete(offer);
    }

    private PersonalizedOfferDTO convertToDTO(PersonalizedOffer offer) {
        PersonalizedOfferDTO dto = new PersonalizedOfferDTO();
        dto.setId(offer.getId());
        dto.setUserId(offer.getKeycloakUserId());
        dto.setProductName(offer.getProductName());
        dto.setQuantity(offer.getQuantity());
        dto.setDeliveryTerms(offer.getDeliveryTerms());
        dto.setPaymentTerms(offer.getPaymentTerms());
        dto.setAdditionalNotes(offer.getAdditionalNotes());
        dto.setCreatedAt(offer.getCreatedAt());
        dto.setStatus(offer.getStatus().name());
        dto.setManagerResponse(offer.getManagerResponse());
        dto.setProposedPrice(offer.getProposedPrice().doubleValue());
        return dto;
    }
} 