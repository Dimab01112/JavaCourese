package com.example.ECommerce.controller;

import com.example.ECommerce.dto.OrderDTO;
import com.example.ECommerce.dto.OrderPlacementDTO;
import com.example.ECommerce.entity.Order;
import com.example.ECommerce.mapper.OrderMapper;
import com.example.ECommerce.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController extends BaseController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderMapper orderMapper;

    @PostMapping("/place")
    public ResponseEntity<OrderDTO> placeOrder(@RequestBody OrderPlacementDTO orderPlacementDTO) {
        try {
            String userId = getCurrentUserId();
            if (userId == null) {
                return ResponseEntity.badRequest().build();
            }
            Order order = orderService.placeOrder(userId, orderPlacementDTO);
            return ResponseEntity.ok(orderMapper.toDTO(order));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Cart is empty") || 
                e.getMessage().equals("Invalid payment method") ||
                e.getMessage().equals("User not found")) {
                return ResponseEntity.badRequest().build();
            }
            throw e;
        }
    }

    @PutMapping("/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderDTO> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam Order.OrderStatus status) {
        try {
            Order updatedOrder = orderService.updateOrderStatus(orderId, status);
            return ResponseEntity.ok(orderMapper.toDTO(updatedOrder));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Order not found")) {
                return ResponseEntity.notFound().build();
            }
            throw e;
        }
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<OrderDTO>> getCurrentUserOrders() {
        String userId = getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.badRequest().build();
        }
        List<Order> orders = orderService.getOrdersByUser(userId);
        return ResponseEntity.ok(orderMapper.toDTOList(orders));
    }

    @GetMapping("/{orderId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long orderId) {
        try {
            Order order = orderService.getOrderById(orderId);
            return ResponseEntity.ok(orderMapper.toDTO(order));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Order not found")) {
                return ResponseEntity.notFound().build();
            }
            throw e;
        }
    }

    @DeleteMapping("/{orderId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long orderId) {
        try {
            orderService.deleteOrder(orderId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Order not found")) {
                return ResponseEntity.notFound().build();
            }
            throw e;
        }
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OrderDTO> createOrder() {
        String userId = getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.badRequest().build();
        }
        Order order = new Order();
        order.setKeycloakUserId(userId);
        Order createdOrder = orderService.createOrder(order);
        return ResponseEntity.ok(orderMapper.toDTO(createdOrder));
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        try {
            List<Order> orders = orderService.getAllOrders();
            return ResponseEntity.ok(orderMapper.toDTOList(orders));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 