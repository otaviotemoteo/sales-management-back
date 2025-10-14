package com.sales.management.model.enums;

public enum PaymentStatus {
    PAID("Pago"),
    PENDING("Pendente");
    
    private final String displayName;
    
    PaymentStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public boolean isPaid() {
        return this == PAID;
    }
}
