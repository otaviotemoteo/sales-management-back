package com.sales.management.util;

import com.sales.management.exception.BadRequestException;

import java.util.regex.Pattern;

public class ValidationUtil {
    
    private static final Pattern EMAIL_PATTERN = 
            Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    
    private static final Pattern PHONE_PATTERN = 
            Pattern.compile("^\\(?\\d{2}\\)?[\\s-]?\\d{4,5}-?\\d{4}$");
    
    public static void validateEmail(String email) {
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new BadRequestException("Email inválido");
        }
    }
    
    public static void validatePhone(String phone) {
        if (phone == null || !PHONE_PATTERN.matcher(phone).matches()) {
            throw new BadRequestException("Telefone inválido");
        }
    }
    
    public static void validateNotEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new BadRequestException(fieldName + " não pode ser vazio");
        }
    }
    
    public static void validatePositive(Number value, String fieldName) {
        if (value == null || value.doubleValue() <= 0) {
            throw new BadRequestException(fieldName + " deve ser maior que zero");
        }
    }
    
    private ValidationUtil() {
        // Private constructor
    }
}