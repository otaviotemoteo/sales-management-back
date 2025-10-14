package com.sales.management.model.enums;

public enum SaleStatus {
    COMPLETED("Concluída"),
    CANCELLED("Cancelada");
    
    private final String displayName;
    
    SaleStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public boolean isActive() {
        return this == COMPLETED;
    }
}