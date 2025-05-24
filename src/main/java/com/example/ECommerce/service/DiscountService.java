package com.example.ECommerce.service;

import com.example.ECommerce.entity.Discount;
import com.example.ECommerce.repository.DiscountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DiscountService {

    @Autowired
    private DiscountRepository discountRepository;

    public Discount addDiscount(Discount discount) {
        return discountRepository.save(discount);
    }

    public List<Discount> getAllDiscounts() {
        return discountRepository.findAll();
    }

    public Discount getDiscountById(Long discountId) {
        return discountRepository.findById(discountId).orElseThrow(() -> new RuntimeException("Discount not found"));
    }

    public void deleteDiscount(Long discountId) {
        discountRepository.deleteById(discountId);
    }
} 