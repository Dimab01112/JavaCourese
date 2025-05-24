package com.example.ECommerce.mapper;

import com.example.ECommerce.dto.OrderDTO;
import com.example.ECommerce.dto.OrderDetailDTO;
import com.example.ECommerce.entity.Order;
import com.example.ECommerce.entity.OrderDetail;
import com.example.ECommerce.entity.KeycloakUser;
import com.example.ECommerce.service.KeycloakUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;
import java.math.BigDecimal;

@Component
public class OrderMapper {
    
    private final OrderDetailMapper orderDetailMapper;
    
    @Autowired
    private KeycloakUserService keycloakUserService;

    public OrderMapper(OrderDetailMapper orderDetailMapper) {
        this.orderDetailMapper = orderDetailMapper;
    }

    public OrderDTO toDTO(Order order) {
        if (order == null) {
            return null;
        }

        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setKeycloakUserId(order.getKeycloakUserId());
        
        KeycloakUser user = keycloakUserService.getUserFromKeycloak(order.getKeycloakUserId());
        if (user != null) {
            dto.setUserName(user.getFirstName() + " " + user.getLastName());
            dto.setUserEmail(user.getEmail());
        }
        
        dto.setStatus(order.getStatus());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setShippingAddress(order.getShippingAddress());
        dto.setBillingAddress(order.getBillingAddress());
        
        if (order.getOrderDetails() != null) {
            List<OrderDetailDTO> orderDetailDTOs = order.getOrderDetails().stream()
                .map(orderDetailMapper::toDTO)
                .collect(Collectors.toList());
            dto.setOrderDetails(orderDetailDTOs);
        }

        return dto;
    }

    public Order toEntity(OrderDTO dto) {
        if (dto == null) {
            return null;
        }

        Order order = new Order();
        order.setId(dto.getId());
        order.setKeycloakUserId(dto.getKeycloakUserId());
        order.setStatus(dto.getStatus());
        order.setCreatedAt(dto.getCreatedAt());
        order.setTotalAmount(dto.getTotalAmount());
        order.setShippingAddress(dto.getShippingAddress());
        order.setBillingAddress(dto.getBillingAddress());

        return order;
    }

    public List<OrderDTO> toDTOList(List<Order> orders) {
        if (orders == null) {
            return null;
        }
        return orders.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
} 