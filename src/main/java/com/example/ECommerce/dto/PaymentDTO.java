package com.example.ECommerce.dto;

import com.example.ECommerce.entity.Payment;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PaymentDTO {
    private Long paymentId;
    private Long orderId;
    private Payment.PaymentMethod paymentMethod;
    private Float amount;
    private Payment.PaymentStatus status;
    private LocalDateTime paymentDate;
    private String transactionId;
    private String cardLastFourDigits;
    private String cardHolderName;
    private String cardExpiryDate;
    private String cardType;
    private String billingAddress;
    private String billingCity;
    private String billingState;
    private String billingZipCode;
    private String billingCountry;
} 