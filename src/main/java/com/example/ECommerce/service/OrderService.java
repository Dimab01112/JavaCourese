package com.example.ECommerce.service;

import com.example.ECommerce.dto.OrderPlacementDTO;
import com.example.ECommerce.entity.*;
import com.example.ECommerce.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final KeycloakUserService keycloakUserService;
    private final CartRepository cartRepository;
    private final PaymentRepository paymentRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository, 
                       KeycloakUserService keycloakUserService,
                       CartRepository cartRepository,
                       PaymentRepository paymentRepository) {
        this.orderRepository = orderRepository;
        this.keycloakUserService = keycloakUserService;
        this.cartRepository = cartRepository;
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public Order placeOrder(String userId, OrderPlacementDTO orderPlacementDTO) {
        // Validate user
        KeycloakUser user = keycloakUserService.getUserFromKeycloak(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        if (!user.isEnabled()) {
            throw new RuntimeException("User account is disabled");
        }

        // Get user's cart
        Cart cart = cartRepository.findByKeycloakUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        // Validate cart is not empty
        if (cart.getCartItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        // Validate payment method
        Payment.PaymentMethod paymentMethod;
        try {
            paymentMethod = Payment.PaymentMethod.valueOf(orderPlacementDTO.getPaymentMethod().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid payment method. Must be one of: CREDIT_CARD, DEBIT_CARD, PAYPAL, BANK_TRANSFER");
        }

        // Create order
        Order order = new Order();
        order.setKeycloakUserId(user.getId());
        order.setStatus(Order.OrderStatus.NEW);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        order.setShippingAddress(orderPlacementDTO.getShippingAddress());
        order.setBillingAddress(orderPlacementDTO.getBillingAddress());

        // Convert cart items to order details
        List<OrderDetail> orderDetails = cart.getCartItems().stream()
                .map(cartItem -> {
                    OrderDetail orderDetail = new OrderDetail();
                    orderDetail.setOrder(order);
                    orderDetail.setProduct(cartItem.getProduct());
                    orderDetail.setQuantity(cartItem.getQuantity());
                    orderDetail.setPrice(cartItem.getProduct().getPrice());
                    return orderDetail;
                })
                .toList();
        order.setOrderDetails(orderDetails);

        // Calculate total price
        float totalPrice = orderDetails.stream()
                .map(detail -> detail.getPrice() * detail.getQuantity())
                .reduce(0.0f, Float::sum);
        order.setTotalAmount(totalPrice);

        // Create payment
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(totalPrice);
        payment.setPaymentMethod(paymentMethod);
        payment.setStatus(Payment.PaymentStatus.PENDING);
        payment.setPaymentDate(LocalDateTime.now());

        // Save order and payment
        Order savedOrder = orderRepository.save(order);
        paymentRepository.save(payment);

        // Clear cart
        cart.getCartItems().clear();
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);

        return savedOrder;
    }

    public Order updateOrderStatus(Long orderId, Order.OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(status);
        return orderRepository.save(order);
    }

    @Transactional
    public List<Order> getOrdersByUser(String userId) {
        // Validate user
        KeycloakUser user = keycloakUserService.getUserFromKeycloak(userId);
        if (!user.isEnabled()) {
            throw new RuntimeException("User account is disabled");
        }
        return orderRepository.findByKeycloakUserId(user.getId());
    }

    @Transactional
    public Order createOrder(Order order) {
        // Validate user
        KeycloakUser user = keycloakUserService.getUserFromKeycloak(order.getKeycloakUserId());
        if (!user.isEnabled()) {
            throw new RuntimeException("User account is disabled");
        }
        return orderRepository.save(order);
    }

    @Transactional
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    @Transactional
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public void deleteOrder(Long orderId) {
        orderRepository.deleteById(orderId);
    }
} 