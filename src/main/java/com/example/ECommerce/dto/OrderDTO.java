package com.example.ECommerce.dto;

import com.example.ECommerce.entity.Order;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDTO {
    private Long id;
    private String keycloakUserId;
    private String userName;
    private String userEmail;
    private Order.OrderStatus status;
    private LocalDateTime createdAt;
    private List<OrderDetailDTO> orderDetails;
    private Float totalAmount;
    private String shippingAddress;
    private String billingAddress;
} 