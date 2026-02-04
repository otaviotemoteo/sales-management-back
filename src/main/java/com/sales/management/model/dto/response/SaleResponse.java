package com.sales.management.model.dto.response;

import com.sales.management.model.enums.PaymentMethod;
import com.sales.management.model.enums.PaymentStatus;
import com.sales.management.model.enums.SaleStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleResponse {
    private Long id;
    private LocalDateTime saleDate;
    private BigDecimal totalAmount;
    private BigDecimal discount;
    private BigDecimal finalAmount;
    private SaleStatus status;
    private String notes;
    
    private UserResponse seller;
    private CustomerResponse customer;
    
    private List<SaleItemResponse> items;
    
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private LocalDateTime paymentDate;
    
    private LocalDateTime createdAt;
}