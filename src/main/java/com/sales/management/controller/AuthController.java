package com.sales.management.controller;

import com.sales.management.model.dto.request.LoginRequest;
import com.sales.management.model.dto.request.RegisterRequest;
import com.sales.management.model.dto.response.AuthResponse;
import com.sales.management.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints de autenticação")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Registrar novo usuário")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Login de usuário")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/check-email")
    @Operation(summary = "Verificar se o e-mail é um primeiro acesso de vendedor")
    public ResponseEntity<Map<String, Boolean>> checkEmail(@RequestBody Map<String, String> request) {
        boolean firstAccess = authService.isFirstAccess(request.get("email"));
        return ResponseEntity.ok(Map.of("firstAccess", firstAccess));
    }

    @PostMapping("/first-access")
    @Operation(summary = "Primeiro acesso de vendedor (sem senha)")
    public ResponseEntity<AuthResponse> firstAccess(@RequestBody Map<String, String> request) {
        return ResponseEntity.ok(authService.firstAccess(request.get("email")));
    }

    @PostMapping("/validate")
    @Operation(summary = "Validar token JWT")
    public ResponseEntity<Map<String, Boolean>> validateToken(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        boolean isValid = authService.validateToken(token);
        return ResponseEntity.ok(Map.of("valid", isValid));
    }
}