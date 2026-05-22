package com.sales.management.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SellerStatsResponse {
    private Long totalSales;
    private BigDecimal totalRevenue;
    private Long totalCustomers;
    private BigDecimal averageTicket;
    private BigDecimal rating;

    public SellerStatsResponse(Long totalSales, BigDecimal totalRevenue, Long totalCustomers, BigDecimal averageTicket) {
        this.totalSales = totalSales;
        this.totalRevenue = totalRevenue;
        this.totalCustomers = totalCustomers;
        this.averageTicket = averageTicket;
        this.rating = BigDecimal.ZERO;
    }
}
