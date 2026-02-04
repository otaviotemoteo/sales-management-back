package com.sales.management.model.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleItemRequest {

    @NotNull(message = "Produto é obrigatório")
    private Long productId;

    @NotNull(message = "Quantidade é obrigatória")
    @Min(value = 1, message = "Quantidade deve ser pelo menos 1")
    private Integer quantity;

    @NotNull(message = "Preço unitário é obrigatório")
    @Min(value = 0, message = "Preço deve ser positivo")
    private BigDecimal unitPrice;
}