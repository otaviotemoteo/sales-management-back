package com.sales.management.service;

import com.sales.management.model.dto.response.DashboardResponse;
import com.sales.management.model.entity.Sale;
import com.sales.management.model.entity.SaleItem;
import com.sales.management.model.enums.PaymentStatus;
import com.sales.management.model.enums.SaleStatus;
import com.sales.management.repository.SaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final SaleRepository saleRepository;

    @Cacheable(value = "dashboard", key = "#startDate + '_' + #endDate + '_' + #sellerId")
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

        // Crescimento vs janela anterior equivalente
        applyGrowth(topProducts, startDate, endDate, sellerId);

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
        return sales.stream()
                .filter(s -> s.getStatus() != SaleStatus.CANCELLED)
                .flatMap(s -> s.getItems().stream())
                .collect(Collectors.groupingBy(
                        item -> item.getProduct().getId(),
                        Collectors.collectingAndThen(Collectors.toList(), items -> {
                            long qty = items.stream().mapToLong(SaleItem::getQuantity).sum();
                            BigDecimal revenue = items.stream()
                                    .map(SaleItem::getTotalPrice)
                                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                            return new Object[]{
                                    items.get(0).getProduct().getId(),
                                    items.get(0).getProduct().getName(),
                                    qty,
                                    revenue
                            };
                        })
                ))
                .values().stream()
                .sorted(Comparator.comparing((Object[] arr) -> (BigDecimal) arr[3]).reversed())
                .limit(5)
                .map(arr -> DashboardResponse.TopProductDTO.builder()
                        .productId((Long) arr[0])
                        .productName((String) arr[1])
                        .quantity((Long) arr[2])
                        .revenue((BigDecimal) arr[3])
                        .growth(BigDecimal.ZERO)
                        .build())
                .toList();
    }

    private void applyGrowth(
            List<DashboardResponse.TopProductDTO> topProducts,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Long sellerId
    ) {
        if (topProducts.isEmpty()) {
            return;
        }

        LocalDateTime prevEnd = startDate;
        LocalDateTime prevStart = startDate.minus(Duration.between(startDate, endDate));

        List<Sale> prevSales = (sellerId != null)
                ? saleRepository.findBySellerIdAndSaleDateBetween(sellerId, prevStart, prevEnd)
                : saleRepository.findBySaleDateBetween(prevStart, prevEnd);

        Map<Long, BigDecimal> prevRevenueByProduct = prevSales.stream()
                .filter(s -> s.getStatus() != SaleStatus.CANCELLED)
                .flatMap(s -> s.getItems().stream())
                .collect(Collectors.groupingBy(
                        i -> i.getProduct().getId(),
                        Collectors.mapping(SaleItem::getTotalPrice,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ));

        topProducts.forEach(tp -> {
            BigDecimal prev = prevRevenueByProduct.getOrDefault(tp.getProductId(), BigDecimal.ZERO);
            BigDecimal growth;
            if (prev.compareTo(BigDecimal.ZERO) == 0) {
                growth = BigDecimal.ZERO;
            } else {
                growth = tp.getRevenue().subtract(prev)
                        .divide(prev, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));
            }
            tp.setGrowth(growth);
        });
    }

    private List<DashboardResponse.SalesTrendDTO> calculateSalesTrend(List<Sale> sales) {
        // Implementar lógica de agregação por data
        return List.of();
    }
}