package com.example.ECommerce.mapper;

import com.example.ECommerce.dto.OrderDetailDTO;
import com.example.ECommerce.entity.OrderDetail;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderDetailMapper {
    
    public OrderDetailDTO toDTO(OrderDetail orderDetail) {
        if (orderDetail == null) {
            return null;
        }

        OrderDetailDTO dto = new OrderDetailDTO();
        dto.setOrderDetailId(orderDetail.getId());
        dto.setOrderId(orderDetail.getOrder().getId());
        dto.setProductId(orderDetail.getProduct().getProductId());
        dto.setProductName(orderDetail.getProduct().getName());
        dto.setQuantity(orderDetail.getQuantity());
        dto.setPrice(orderDetail.getPrice());
        dto.setSubtotal(orderDetail.getPrice() * orderDetail.getQuantity());
        return dto;
    }

    public OrderDetail toEntity(OrderDetailDTO dto) {
        if (dto == null) {
            return null;
        }

        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setId(dto.getOrderDetailId());
        orderDetail.setQuantity(dto.getQuantity());
        orderDetail.setPrice(dto.getPrice());
        return orderDetail;
    }

    public List<OrderDetailDTO> toDTOList(List<OrderDetail> orderDetails) {
        if (orderDetails == null) {
            return null;
        }
        return orderDetails.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
} 