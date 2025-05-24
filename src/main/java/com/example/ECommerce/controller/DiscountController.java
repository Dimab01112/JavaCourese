package com.example.ECommerce.controller;

import com.example.ECommerce.dto.DiscountDTO;
import com.example.ECommerce.entity.Discount;
import com.example.ECommerce.mapper.DiscountMapper;
import com.example.ECommerce.service.DiscountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/discounts")
public class DiscountController {

    @Autowired
    private DiscountService discountService;

    @Autowired
    private DiscountMapper discountMapper;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<DiscountDTO> addDiscount(@RequestBody DiscountDTO discountDTO) {
        Discount discount = discountMapper.toEntity(discountDTO);
        Discount savedDiscount = discountService.addDiscount(discount);
        return ResponseEntity.ok(discountMapper.toDTO(savedDiscount));
    }

    @GetMapping
    public ResponseEntity<List<DiscountDTO>> getAllDiscounts() {
        List<Discount> discounts = discountService.getAllDiscounts();
        return ResponseEntity.ok(discountMapper.toDTOList(discounts));
    }

    @GetMapping("/{discountId}")
    public ResponseEntity<DiscountDTO> getDiscountById(@PathVariable Long discountId) {
        Discount discount = discountService.getDiscountById(discountId);
        return ResponseEntity.ok(discountMapper.toDTO(discount));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{discountId}")
    public ResponseEntity<Void> deleteDiscount(@PathVariable Long discountId) {
        discountService.deleteDiscount(discountId);
        return ResponseEntity.ok().build();
    }
} 