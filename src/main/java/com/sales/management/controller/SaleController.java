package com.sales.management.controller;

import com.sales.management.model.dto.request.CreateSaleRequest;
import com.sales.management.model.dto.request.UpdateSaleRequest;
import com.sales.management.model.dto.response.SaleResponse;
import com.sales.management.service.SaleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/sales")
@RequiredArgsConstructor
@Tag(name = "Sales", description = "Gerenciamento de vendas")
@SecurityRequirement(name = "bearerAuth")
public class SaleController {

    private final SaleService saleService;

    @PostMapping
    @Operation(summary = "Criar nova venda")
    public ResponseEntity<SaleResponse> createSale(@Valid @RequestBody CreateSaleRequest request) {
        return new ResponseEntity<>(saleService.createSale(request), HttpStatus.CREATED);
    }

    @GetMapping("/my-sales")
    @Operation(summary = "Listar minhas vendas")
    public ResponseEntity<Page<SaleResponse>> getMySales(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "saleDate"));
        return ResponseEntity.ok(saleService.getMySales(pageable));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar todas as vendas (Admin only)")
    public ResponseEntity<Page<SaleResponse>> getAllSales(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "saleDate"));
        return ResponseEntity.ok(saleService.getAllSales(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar venda por ID")
    public ResponseEntity<SaleResponse> getSaleById(@PathVariable Long id) {
        return ResponseEntity.ok(saleService.getSaleById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar venda")
    public ResponseEntity<SaleResponse> updateSale(
            @PathVariable Long id,
            @Valid @RequestBody UpdateSaleRequest request
    ) {
        return ResponseEntity.ok(saleService.updateSale(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancelar venda")
    public ResponseEntity<Void> cancelSale(@PathVariable Long id) {
        saleService.cancelSale(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/payment/mark-paid")
    @Operation(summary = "Marcar pagamento como pago")
    public ResponseEntity<SaleResponse> markPaymentAsPaid(@PathVariable Long id) {
        return ResponseEntity.ok(saleService.markPaymentAsPaid(id));
    }

    @GetMapping("/customer/{customerId}/statement")
    @Operation(summary = "Obter extrato de vendas do cliente")
    public ResponseEntity<List<SaleResponse>> getCustomerStatement(
            @PathVariable Long customerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate
    ) {
        return ResponseEntity.ok(saleService.getCustomerSalesInPeriod(customerId, startDate, endDate));
    }
}