package com.sales.management.model.enums;

public enum PaymentMethod {
    CASH("Dinheiro"),
    PIX("PIX"),
    DEBIT_CARD("Cartão de Débito"),
    CREDIT_CARD("Cartão de Crédito");
    
    private final String displayName;
    
    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
   
    //quando é instantâneo
    public boolean isInstantPayment() {
        return this == CASH || this == DEBIT_CARD;
    }
}
