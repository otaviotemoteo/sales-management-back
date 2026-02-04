package com.sales.management.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {
    private BigDecimal totalSalesAmount;
    private Long salesCount;
    private BigDecimal pendingPaymentsAmount;
    private Long pendingPaymentsCount;
    
    private List<TopProductDTO> topProducts;
    private Map<String, Long> salesByPaymentMethod;
    private List<SalesTrendDTO> salesTrend;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopProductDTO {
        private Long productId;
        private String productName;
        private Long quantity;
        private BigDecimal revenue;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SalesTrendDTO {
        private String date;
        private BigDecimal amount;
        private Long count;
    }
}