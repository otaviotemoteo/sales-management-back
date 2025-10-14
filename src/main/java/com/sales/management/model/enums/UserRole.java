package com.sales.management.model.enums;

public enum UserRole {
    ADMIN("Administrador"),
    SELLER("Vendedor");
    
    private final String displayName;
    
    UserRole(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
