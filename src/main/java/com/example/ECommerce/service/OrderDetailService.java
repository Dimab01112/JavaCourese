package com.example.ECommerce.service;

import com.example.ECommerce.entity.OrderDetail;
import com.example.ECommerce.repository.OrderDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class OrderDetailService {

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    public OrderDetail addOrderDetail(OrderDetail orderDetail) {
        return orderDetailRepository.save(orderDetail);
    }

    public List<OrderDetail> getOrderDetailsByOrderId(Long orderId) {
        return orderDetailRepository.findByOrder_Id(orderId);
    }

    public void deleteOrderDetail(Long orderDetailId) {
        orderDetailRepository.deleteById(orderDetailId);
    }
} 