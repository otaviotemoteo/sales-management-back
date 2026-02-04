package com.sales.management.model.dto.request;

import com.sales.management.model.enums.PaymentMethod;
import com.sales.management.model.enums.PaymentStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateSaleRequest {

    @NotNull(message = "Cliente é obrigatório")
    private Long customerId;

    @NotEmpty(message = "Deve haver pelo menos um item")
    @Valid
    private List<SaleItemRequest> items;

    @NotNull(message = "Método de pagamento é obrigatório")
    private PaymentMethod paymentMethod;

    @NotNull(message = "Status de pagamento é obrigatório")
    private PaymentStatus paymentStatus;

    private BigDecimal discount;

    private String notes;
}