package com.sales.management.repository;

import com.sales.management.model.entity.SaleItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SaleItemRepository extends JpaRepository<SaleItem, Long> {
    
    List<SaleItem> findBySaleId(Long saleId);
    
    @Query("SELECT si FROM SaleItem si JOIN si.sale s " +
           "WHERE s.saleDate BETWEEN :startDate AND :endDate " +
           "GROUP BY si.product.id " +
           "ORDER BY SUM(si.quantity) DESC")
    List<SaleItem> findTopSellingProducts(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
}