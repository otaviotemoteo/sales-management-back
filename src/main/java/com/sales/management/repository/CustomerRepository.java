package com.sales.management.repository;

import com.sales.management.model.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    
    @Query("SELECT c FROM Customer c WHERE " +
           "LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "c.phone LIKE CONCAT('%', :search, '%')")
    Page<Customer> searchCustomers(@Param("search") String search, Pageable pageable);
    
    Page<Customer> findByNameContainingIgnoreCase(String name, Pageable pageable);
    
    List<Customer> findByPhoneContaining(String phone);
    
    boolean existsByPhone(String phone);
}