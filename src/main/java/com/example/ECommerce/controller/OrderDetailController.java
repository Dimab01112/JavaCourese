package com.example.ECommerce.controller;

import com.example.ECommerce.dto.OrderDetailDTO;
import com.example.ECommerce.entity.OrderDetail;
import com.example.ECommerce.mapper.OrderDetailMapper;
import com.example.ECommerce.service.OrderDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/order-details")
public class OrderDetailController extends BaseController {

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderDetailDTO> addOrderDetail(@RequestBody OrderDetailDTO orderDetailDTO) {
        try {
            OrderDetail orderDetail = orderDetailMapper.toEntity(orderDetailDTO);
            OrderDetail savedOrderDetail = orderDetailService.addOrderDetail(orderDetail);
            return ResponseEntity.ok(orderDetailMapper.toDTO(savedOrderDetail));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Order not found") || 
                e.getMessage().equals("Product not found")) {
                return ResponseEntity.badRequest().build();
            }
            throw e;
        }
    }

    @GetMapping("/order/{orderId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<OrderDetailDTO>> getOrderDetailsByOrderId(@PathVariable Long orderId) {
        try {
            List<OrderDetail> orderDetails = orderDetailService.getOrderDetailsByOrderId(orderId);
            List<OrderDetailDTO> orderDetailDTOs = orderDetails.stream()
                .map(orderDetailMapper::toDTO)
                .collect(Collectors.toList());
            return ResponseEntity.ok(orderDetailDTOs);
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Order not found")) {
                return ResponseEntity.notFound().build();
            }
            throw e;
        }
    }

    @DeleteMapping("/{orderDetailId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteOrderDetail(@PathVariable Long orderDetailId) {
        try {
            orderDetailService.deleteOrderDetail(orderDetailId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Order detail not found")) {
                return ResponseEntity.notFound().build();
            }
            throw e;
        }
    }
} 