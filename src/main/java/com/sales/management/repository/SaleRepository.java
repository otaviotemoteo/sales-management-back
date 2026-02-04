package com.sales.management.repository;

import com.sales.management.model.entity.Sale;
import com.sales.management.model.enums.SaleStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {
    
    Page<Sale> findBySellerId(Long sellerId, Pageable pageable);
    
    Page<Sale> findByCustomerId(Long customerId, Pageable pageable);
    
    Page<Sale> findByStatus(SaleStatus status, Pageable pageable);
    
    @Query("SELECT s FROM Sale s WHERE s.seller.id = :sellerId " +
           "AND s.saleDate BETWEEN :startDate AND :endDate")
    List<Sale> findBySellerIdAndSaleDateBetween(
        @Param("sellerId") Long sellerId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    @Query("SELECT s FROM Sale s WHERE s.saleDate BETWEEN :startDate AND :endDate")
    List<Sale> findBySaleDateBetween(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    @Query("SELECT s FROM Sale s WHERE s.customer.id = :customerId " +
           "AND s.saleDate BETWEEN :startDate AND :endDate " +
           "ORDER BY s.saleDate DESC")
    List<Sale> findCustomerSalesInPeriod(
        @Param("customerId") Long customerId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
}