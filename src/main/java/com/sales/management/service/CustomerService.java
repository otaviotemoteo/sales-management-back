package com.sales.management.service;

import com.sales.management.exception.ResourceNotFoundException;
import com.sales.management.model.dto.request.CreateCustomerRequest;
import com.sales.management.model.dto.request.UpdateCustomerRequest;
import com.sales.management.model.dto.response.CustomerResponse;
import com.sales.management.model.entity.Customer;
import com.sales.management.model.entity.User;
import com.sales.management.repository.CustomerRepository;
import com.sales.management.util.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final UserService userService;

    @Transactional
    public CustomerResponse createCustomer(CreateCustomerRequest request) {
        User currentUser = getCurrentUser();

        Customer customer = Customer.builder()
                .name(request.getName())
                .phone(request.getPhone())
                .email(request.getEmail())
                .address(request.getAddress())
                .createdBy(currentUser)
                .build();

        customer = customerRepository.save(customer);
        return mapToResponse(customer);
    }

    @Transactional
    public CustomerResponse updateCustomer(Long id, UpdateCustomerRequest request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.CUSTOMER_NOT_FOUND));

        if (request.getName() != null) {
            customer.setName(request.getName());
        }
        if (request.getPhone() != null) {
            customer.setPhone(request.getPhone());
        }
        if (request.getEmail() != null) {
            customer.setEmail(request.getEmail());
        }
        if (request.getAddress() != null) {
            customer.setAddress(request.getAddress());
        }

        customer = customerRepository.save(customer);
        return mapToResponse(customer);
    }

    @Transactional
    public void deleteCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.CUSTOMER_NOT_FOUND));
        customerRepository.delete(customer);
    }

    public CustomerResponse getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.CUSTOMER_NOT_FOUND));
        return mapToResponse(customer);
    }

    public Page<CustomerResponse> getAllCustomers(Pageable pageable) {
        return customerRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    public Page<CustomerResponse> searchCustomers(String search, Pageable pageable) {
        return customerRepository.findByNameContainingIgnoreCase(search, pageable)
                .map(this::mapToResponse);
    }

    private CustomerResponse mapToResponse(Customer customer) {
        return CustomerResponse.builder()
                .id(customer.getId())
                .name(customer.getName())
                .phone(customer.getPhone())
                .email(customer.getEmail())
                .address(customer.getAddress())
                .createdByUsername(customer.getCreatedBy().getUsername())
                .createdAt(customer.getCreatedAt())
                .updatedAt(customer.getUpdatedAt())
                .build();
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userService.findByEmail(username);
    }
}
