package com.sales.management.util;

public class Constants {
    
    // JWT
    public static final String JWT_HEADER = "Authorization";
    public static final String JWT_PREFIX = "Bearer ";
    
    // Roles
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_SELLER = "SELLER";
    
    // Messages
    public static final String USER_NOT_FOUND = "Usuário não encontrado";
    public static final String PRODUCT_NOT_FOUND = "Produto não encontrado";
    public static final String CUSTOMER_NOT_FOUND = "Cliente não encontrado";
    public static final String SALE_NOT_FOUND = "Venda não encontrada";
    public static final String EMAIL_ALREADY_EXISTS = "Email já cadastrado";
    public static final String INVALID_CREDENTIALS = "Email ou senha inválidos";
    public static final String UNAUTHORIZED_ACCESS = "Acesso não autorizado";
    
    // Validation
    public static final int MIN_PASSWORD_LENGTH = 6;
    public static final int MAX_NAME_LENGTH = 100;
    public static final int MAX_EMAIL_LENGTH = 100;
    
    private Constants() {
        // Private constructor to prevent instantiation
    }
}