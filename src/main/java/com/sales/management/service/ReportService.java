package com.sales.management.service;

import com.sales.management.model.dto.response.DashboardResponse;
import com.sales.management.model.entity.Sale;
import com.sales.management.model.enums.PaymentStatus;
import com.sales.management.repository.SaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final SaleRepository saleRepository;

    public DashboardResponse getDashboardMetrics(LocalDateTime startDate, LocalDateTime endDate, Long sellerId) {
        List<Sale> sales;
        
        if (sellerId != null) {
            sales = saleRepository.findBySellerIdAndSaleDateBetween(sellerId, startDate, endDate);
        } else {
            sales = saleRepository.findBySaleDateBetween(startDate, endDate);
        }

        BigDecimal totalSalesAmount = sales.stream()
                .map(Sale::getFinalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Long salesCount = (long) sales.size();

        BigDecimal pendingPaymentsAmount = sales.stream()
                .filter(s -> s.getPayment().getPaymentStatus() == PaymentStatus.PENDING)
                .map(Sale::getFinalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Long pendingPaymentsCount = sales.stream()
                .filter(s -> s.getPayment().getPaymentStatus() == PaymentStatus.PENDING)
                .count();

        // Calcular produtos mais vendidos
        List<DashboardResponse.TopProductDTO> topProducts = calculateTopProducts(sales);

        // Vendas por método de pagamento
        Map<String, Long> salesByPaymentMethod = sales.stream()
                .collect(Collectors.groupingBy(
                    s -> s.getPayment().getPaymentMethod().toString(),
                    Collectors.counting()
                ));

        // Tendência de vendas (por dia)
        List<DashboardResponse.SalesTrendDTO> salesTrend = calculateSalesTrend(sales);

        return DashboardResponse.builder()
                .totalSalesAmount(totalSalesAmount)
                .salesCount(salesCount)
                .pendingPaymentsAmount(pendingPaymentsAmount)
                .pendingPaymentsCount(pendingPaymentsCount)
                .topProducts(topProducts)
                .salesByPaymentMethod(salesByPaymentMethod)
                .salesTrend(salesTrend)
                .build();
    }

    private List<DashboardResponse.TopProductDTO> calculateTopProducts(List<Sale> sales) {
        // Implementar lógica de agregação
        return List.of();
    }

    private List<DashboardResponse.SalesTrendDTO> calculateSalesTrend(List<Sale> sales) {
        // Implementar lógica de agregação por data
        return List.of();
    }
}