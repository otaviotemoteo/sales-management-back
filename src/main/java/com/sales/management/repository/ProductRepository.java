package com.sales.management.repository;

import com.sales.management.model.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    Page<Product> findByActiveTrue(Pageable pageable);
    
    Optional<Product> findByIdAndActiveTrue(Long id);
    
    Page<Product> findByCategory(String category, Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE " +
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) AND " +
           "p.active = true")
    Page<Product> searchProducts(@Param("search") String search, Pageable pageable);
    
    @Query("SELECT DISTINCT p.category FROM Product p WHERE p.active = true AND p.category IS NOT NULL")
    List<String> findAllCategories();
}