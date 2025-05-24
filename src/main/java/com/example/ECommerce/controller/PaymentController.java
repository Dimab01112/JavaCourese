package com.example.ECommerce.controller;

import com.example.ECommerce.dto.PaymentDTO;
import com.example.ECommerce.dto.PaymentProcessDTO;
import com.example.ECommerce.entity.Payment;
import com.example.ECommerce.mapper.PaymentMapper;
import com.example.ECommerce.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController extends BaseController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentMapper paymentMapper;

    @PostMapping("/process")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PaymentDTO> processPayment(@RequestBody PaymentProcessDTO paymentProcessDTO) {
        try {
            Payment payment = paymentService.processPayment(paymentProcessDTO);
            return ResponseEntity.ok(paymentMapper.toDTO(payment));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Order not found")) {
                return ResponseEntity.notFound().build();
            }
            if (e.getMessage().equals("Invalid card details") || 
                e.getMessage().equals("Order already paid") ||
                e.getMessage().equals("Invalid payment method")) {
                return ResponseEntity.badRequest().build();
            }
            throw e;
        }
    }

    @GetMapping("/{paymentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PaymentDTO> getPaymentById(@PathVariable Long paymentId) {
        try {
            Payment payment = paymentService.getPaymentById(paymentId);
            return ResponseEntity.ok(paymentMapper.toDTO(payment));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Payment not found")) {
                return ResponseEntity.notFound().build();
            }
            throw e;
        }
    }

    @DeleteMapping("/{paymentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePayment(@PathVariable Long paymentId) {
        try {
            paymentService.deletePayment(paymentId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Payment not found")) {
                return ResponseEntity.notFound().build();
            }
            throw e;
        }
    }
} 