package com.example.ECommerce.mapper;

import com.example.ECommerce.dto.PaymentDTO;
import com.example.ECommerce.entity.Payment;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PaymentMapper {
    
    public PaymentDTO toDTO(Payment payment) {
        if (payment == null) {
            return null;
        }

        PaymentDTO dto = new PaymentDTO();
        dto.setPaymentId(payment.getId());
        dto.setOrderId(payment.getOrder().getId());
        dto.setAmount(payment.getAmount());
        dto.setPaymentMethod(payment.getPaymentMethod());
        dto.setStatus(payment.getStatus());
        dto.setPaymentDate(payment.getPaymentDate());
        return dto;
    }

    public Payment toEntity(PaymentDTO dto) {
        if (dto == null) {
            return null;
        }

        Payment payment = new Payment();
        payment.setId(dto.getPaymentId());
        payment.setAmount(dto.getAmount());
        payment.setPaymentMethod(dto.getPaymentMethod());
        payment.setStatus(dto.getStatus());
        payment.setPaymentDate(dto.getPaymentDate());
        return payment;
    }

    public List<PaymentDTO> toDTOList(List<Payment> payments) {
        if (payments == null) {
            return null;
        }
        return payments.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
} 