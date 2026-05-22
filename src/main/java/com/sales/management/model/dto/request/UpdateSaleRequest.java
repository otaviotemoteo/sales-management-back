package com.sales.management.model.dto.request;

import com.sales.management.model.enums.SaleStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSaleRequest {

    private SaleStatus status;

    private BigDecimal finalAmount;

    private String notes;
}
