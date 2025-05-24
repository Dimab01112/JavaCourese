package com.example.ECommerce.controller;

import com.example.ECommerce.dto.PersonalizedOfferDTO;
import com.example.ECommerce.dto.CreatePersonalizedOfferDTO;
import com.example.ECommerce.dto.UpdatePersonalizedOfferDTO;
import com.example.ECommerce.service.PersonalizedOfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/offers")
public class PersonalizedOfferController extends BaseController {

    @Autowired
    private PersonalizedOfferService offerService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PersonalizedOfferDTO>> getCurrentUserOffers() {
        String userId = getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(offerService.getUserOffers(userId));
    }

    @PostMapping
    @PreAuthorize("hasRole('B2B')")
    public ResponseEntity<PersonalizedOfferDTO> createOffer(@RequestBody CreatePersonalizedOfferDTO offerDTO) {
        String userId = getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(offerService.createOffer(userId, offerDTO));
    }

    @PutMapping("/{offerId}")
    @PreAuthorize("hasRole('B2B')")
    public ResponseEntity<PersonalizedOfferDTO> updateOffer(
            @PathVariable Long offerId,
            @RequestBody UpdatePersonalizedOfferDTO offerDTO) {
        String userId = getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(offerService.updateOffer(offerId, offerDTO));
    }

    @DeleteMapping("/{offerId}")
    @PreAuthorize("hasRole('B2B')")
    public ResponseEntity<Void> deleteOffer(@PathVariable Long offerId) {
        String userId = getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.badRequest().build();
        }
        offerService.deleteOffer(offerId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PersonalizedOfferDTO>> getAllOffers() {
        return ResponseEntity.ok(offerService.getPendingOffers());
    }

    @PutMapping("/admin/{offerId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PersonalizedOfferDTO> updateOfferStatus(
            @PathVariable Long offerId,
            @RequestParam String status) {
        UpdatePersonalizedOfferDTO updateDTO = new UpdatePersonalizedOfferDTO();
        updateDTO.setStatus(status);
        return ResponseEntity.ok(offerService.updateOffer(offerId, updateDTO));
    }
} 