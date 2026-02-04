package com.sales.management.service;

import com.sales.management.exception.BadRequestException;
import com.sales.management.exception.BusinessException;
import com.sales.management.exception.ResourceNotFoundException;
import com.sales.management.exception.UnauthorizedException;
import com.sales.management.model.dto.request.CreateSaleRequest;
import com.sales.management.model.dto.request.UpdateSaleRequest;
import com.sales.management.model.dto.response.*;
import com.sales.management.model.entity.*;
import com.sales.management.model.enums.PaymentStatus;
import com.sales.management.model.enums.SaleStatus;
import com.sales.management.model.enums.UserRole;
import com.sales.management.repository.*;
import com.sales.management.util.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SaleService {

    private final SaleRepository saleRepository;
    private final SaleItemRepository saleItemRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;
    private final ObjectMapper objectMapper;

    @Transactional
    public SaleResponse createSale(CreateSaleRequest request) {
        // Obter usuário logado
        User seller = getCurrentUser();

        // Validar cliente
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException(Constants.CUSTOMER_NOT_FOUND));

        // Validar produtos e calcular total
        BigDecimal totalAmount = BigDecimal.ZERO;
        
        Sale sale = Sale.builder()
                .seller(seller)
                .customer(customer)
                .status(SaleStatus.CONFIRMED)
                .discount(request.getDiscount() != null ? request.getDiscount() : BigDecimal.ZERO)
                .notes(request.getNotes())
                .build();

        // Adicionar items
        for (var itemRequest : request.getItems()) {
            Product product = productRepository.findByIdAndActiveTrue(itemRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException(Constants.PRODUCT_NOT_FOUND));

            SaleItem item = SaleItem.builder()
                    .product(product)
                    .quantity(itemRequest.getQuantity())
                    .unitPrice(itemRequest.getUnitPrice())
                    .build();
            
            sale.addItem(item);
            totalAmount = totalAmount.add(item.getTotalPrice());
        }

        sale.setTotalAmount(totalAmount);
        sale.setFinalAmount(totalAmount.subtract(sale.getDiscount()));

        // Criar pagamento
        Payment payment = Payment.builder()
                .sale(sale)
                .paymentMethod(request.getPaymentMethod())
                .paymentStatus(request.getPaymentStatus())
                .amount(sale.getFinalAmount())
                .build();
        
        if (request.getPaymentStatus() == PaymentStatus.PAID) {
            payment.setPaymentDate(LocalDateTime.now());
        }
        
        sale.setPayment(payment);

        // Salvar
        sale = saleRepository.save(sale);
        
        // Audit: Log sale creation
        try {
            auditLogService.createAuditLog(
                    "SALE",
                    sale.getId(),
                    "CREATE",
                    null,
                    objectMapper.writeValueAsString(sale)
            );
        } catch (Exception e) {
            log.error("Error creating audit log for sale creation", e);
        }
        
        return mapToResponse(sale);
    }

    @Transactional
    public SaleResponse updateSale(Long id, UpdateSaleRequest request) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.SALE_NOT_FOUND));

        // Validar autorização
        validateSaleAccess(sale);

        // Apenas permitir edição se status for PENDING
        if (sale.getStatus() != SaleStatus.PENDING) {
            throw new BusinessException("Apenas vendas pendentes podem ser editadas");
        }

        // Store old value for audit
        try {
            String oldValue = objectMapper.writeValueAsString(sale);
            
            // Atualizar campos...
            // (Similar ao createSale, recalculando totais)
            Sale updatedSale = saleRepository.save(sale);
            
            // Audit: Log sale update
            String newValue = objectMapper.writeValueAsString(updatedSale);
            auditLogService.createAuditLog(
                    "SALE",
                    updatedSale.getId(),
                    "UPDATE",
                    oldValue,
                    newValue
            );
            
            return mapToResponse(updatedSale);
        } catch (Exception e) {
            log.error("Error updating sale or creating audit log", e);
            throw new BusinessException("Erro ao atualizar venda");
        }
    }

    @Transactional
    public void cancelSale(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.SALE_NOT_FOUND));

        validateSaleAccess(sale);

        SaleStatus previousStatus = sale.getStatus();
        sale.setStatus(SaleStatus.CANCELLED);
        saleRepository.save(sale);
        
        // Audit: Log sale cancellation
        try {
            auditLogService.createAuditLog(
                    "SALE",
                    sale.getId(),
                    "CANCEL",
                    previousStatus.toString(),
                    SaleStatus.CANCELLED.toString()
            );
        } catch (Exception e) {
            log.error("Error creating audit log for sale cancellation", e);
        }
    }

    @Transactional
    public SaleResponse markPaymentAsPaid(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.SALE_NOT_FOUND));

        validateSaleAccess(sale);

        Payment payment = sale.getPayment();
        PaymentStatus previousStatus = payment.getPaymentStatus();
        payment.setPaymentStatus(PaymentStatus.PAID);
        payment.setPaymentDate(LocalDateTime.now());

        Sale updatedSale = saleRepository.save(sale);
        
        // Audit: Log payment status change
        try {
            auditLogService.createAuditLog(
                    "PAYMENT",
                    payment.getId(),
                    "UPDATE",
                    previousStatus.toString(),
                    PaymentStatus.PAID.toString()
            );
            
            // Also log sale status change if applicable
            auditLogService.createAuditLog(
                    "SALE",
                    sale.getId(),
                    "PAYMENT_RECEIVED",
                    null,
                    "Payment received at " + payment.getPaymentDate()
            );
        } catch (Exception e) {
            log.error("Error creating audit log for payment status change", e);
        }

        return mapToResponse(updatedSale);
    }

    public SaleResponse getSaleById(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.SALE_NOT_FOUND));
        
        validateSaleAccess(sale);
        
        return mapToResponse(sale);
    }

    public Page<SaleResponse> getMySales(Pageable pageable) {
        User user = getCurrentUser();
        return saleRepository.findBySellerId(user.getId(), pageable)
                .map(this::mapToResponse);
    }

    public Page<SaleResponse> getAllSales(Pageable pageable) {
        return saleRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    public List<SaleResponse> getCustomerSalesInPeriod(Long customerId, LocalDateTime startDate, LocalDateTime endDate) {
        return saleRepository.findCustomerSalesInPeriod(customerId, startDate, endDate)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException(Constants.UNAUTHORIZED_ACCESS));
    }

    private void validateSaleAccess(Sale sale) {
        User currentUser = getCurrentUser();
        
        // Admin pode acessar tudo
        if (currentUser.getRole() == UserRole.ADMIN) {
            return;
        }
        
        // Vendedor só pode acessar suas próprias vendas
        if (!sale.getSeller().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException(Constants.UNAUTHORIZED_ACCESS);
        }
    }

    private SaleResponse mapToResponse(Sale sale) {
        // Implementar mapping completo
        return SaleResponse.builder()
                .id(sale.getId())
                .saleDate(sale.getSaleDate())
                .totalAmount(sale.getTotalAmount())
                .discount(sale.getDiscount())
                .finalAmount(sale.getFinalAmount())
                .status(sale.getStatus())
                .notes(sale.getNotes())
                // ... mapear seller, customer, items, payment
                .createdAt(sale.getCreatedAt())
                .build();
    }
}