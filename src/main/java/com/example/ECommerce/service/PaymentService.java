package com.example.ECommerce.service;

import com.example.ECommerce.dto.PaymentProcessDTO;
import com.example.ECommerce.entity.*;
import com.example.ECommerce.repository.OrderRepository;
import com.example.ECommerce.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

@Service
@Transactional
public class PaymentService {
    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Transactional
    public Payment processPayment(PaymentProcessDTO paymentProcessDTO) {
        logger.info("Processing payment for order ID: {}", paymentProcessDTO.getOrderId());

        // Find order
        Order order = orderRepository.findById(paymentProcessDTO.getOrderId())
                .orElseThrow(() -> {
                    logger.error("Order not found with ID: {}", paymentProcessDTO.getOrderId());
                    return new RuntimeException("Order not found");
                });

        // Check if order is already paid
        if (order.getStatus() == Order.OrderStatus.DELIVERED) {
            logger.error("Order {} is already delivered", order.getId());
            throw new RuntimeException("Order already completed");
        }

        // Validate payment method
        try {
            Payment.PaymentMethod.valueOf(paymentProcessDTO.getPaymentMethod());
        } catch (IllegalArgumentException e) {
            logger.error("Invalid payment method: {}", paymentProcessDTO.getPaymentMethod());
            throw new RuntimeException("Invalid payment method");
        }

        // Validate card details
        if (!isValidCardDetails(paymentProcessDTO.getCardDetails())) {
            logger.error("Invalid card details for order {}", order.getId());
            throw new RuntimeException("Invalid card details");
        }

        // Create or update payment
        Payment payment = paymentRepository.findByOrder_Id(order.getId())
                .orElse(new Payment());

        payment.setOrder(order);
        payment.setAmount(order.getTotalAmount());
        payment.setPaymentMethod(Payment.PaymentMethod.valueOf(paymentProcessDTO.getPaymentMethod()));
        payment.setStatus(Payment.PaymentStatus.COMPLETED);
        payment.setPaymentDate(LocalDateTime.now());

        // Update order status
        order.setStatus(Order.OrderStatus.PROCESSING);
        orderRepository.save(order);

        // Save payment
        logger.info("Payment processed successfully for order {}", order.getId());
        return paymentRepository.save(payment);
    }

    private boolean isValidCardDetails(PaymentProcessDTO.CardDetailsDTO cardDetails) {
        // Basic validation
        if (cardDetails == null ||
                cardDetails.getCardNumber() == null ||
                cardDetails.getExpiryDate() == null ||
                cardDetails.getCvv() == null) {
            logger.error("Card details are null or incomplete");
            return false;
        }

        // Validate card number format and Luhn algorithm
        String cardNumber = cardDetails.getCardNumber().replaceAll("\\s+", "");
        if (!cardNumber.matches("\\d{16}")) {
            logger.error("Invalid card number format: {}", cardNumber);
            return false;
        }
        if (!isValidLuhn(cardNumber)) {
            logger.error("Card number {} failed Luhn algorithm check", cardNumber);
            return false;
        }

        // Validate expiry date format (MM/YY)
        if (!cardDetails.getExpiryDate().matches("\\d{2}/\\d{2}")) {
            logger.error("Invalid expiry date format: {}", cardDetails.getExpiryDate());
            return false;
        }

        // Validate CVV (3 or 4 digits)
        if (!cardDetails.getCvv().matches("\\d{3,4}")) {
            logger.error("Invalid CVV format: {}", cardDetails.getCvv());
            return false;
        }

        return true;
    }

    private boolean isValidLuhn(String cardNumber) {
        int sum = 0;
        boolean alternate = false;

        // Loop through values starting from the rightmost digit
        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int n = Integer.parseInt(cardNumber.substring(i, i + 1));
            if (alternate) {
                n *= 2;
                if (n > 9) {
                    n = (n % 10) + 1;
                }
            }
            sum += n;
            alternate = !alternate;
        }

        return (sum % 10 == 0);
    }

    public Payment getPaymentById(Long paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
    }

    public void deletePayment(Long paymentId) {
        paymentRepository.deleteById(paymentId);
    }

    public Payment refundPayment(Long paymentId) {
        Payment payment = getPaymentById(paymentId);

        // Check if payment is eligible for refund
        if (payment.getStatus() != Payment.PaymentStatus.COMPLETED) {
            throw new RuntimeException("Payment is not eligible for refund");
        }

        // Update payment status
        payment.setStatus(Payment.PaymentStatus.REFUNDED);

        // Update order status
        Order order = payment.getOrder();
        order.setStatus(Order.OrderStatus.CANCELLED);
        orderRepository.save(order);

        return paymentRepository.save(payment);
    }
} 