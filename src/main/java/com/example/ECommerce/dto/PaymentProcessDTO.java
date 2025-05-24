package com.example.ECommerce.dto;

import lombok.Data;

@Data
public class PaymentProcessDTO {
    private Long orderId;
    private String paymentMethod;
    private CardDetailsDTO cardDetails;

    @Data
    public static class CardDetailsDTO {
        private String cardNumber;
        private String expiryDate;
        private String cvv;
    }
} 