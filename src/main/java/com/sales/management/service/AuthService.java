package com.sales.management.service;

import com.sales.management.exception.BadRequestException;
import com.sales.management.model.dto.request.LoginRequest;
import com.sales.management.model.dto.request.RegisterRequest;
import com.sales.management.model.dto.response.AuthResponse;
import com.sales.management.model.dto.response.UserResponse;
import com.sales.management.model.entity.User;
import com.sales.management.repository.UserRepository;
import com.sales.management.security.JwtTokenProvider;
import com.sales.management.util.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Validar se email já existe
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException(Constants.EMAIL_ALREADY_EXISTS);
        }

        // Criar usuário
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .active(true)
                .build();

        user = userRepository.save(user);

        // Gerar token
        String token = jwtTokenProvider.generateToken(user);

        return AuthResponse.builder()
                .token(token)
                .user(mapToUserResponse(user))
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        // Autenticar
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Buscar usuário
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException(Constants.INVALID_CREDENTIALS));

        // Gerar token
        String token = jwtTokenProvider.generateToken(user);

        return AuthResponse.builder()
                .token(token)
                .user(mapToUserResponse(user))
                .build();
    }

    public boolean validateToken(String token) {
        try {
            String email = jwtTokenProvider.extractUsername(token);
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new BadRequestException(Constants.USER_NOT_FOUND));
            return jwtTokenProvider.isTokenValid(token, user);
        } catch (Exception e) {
            return false;
        }
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .active(user.getActive())
                .createdAt(user.getCreatedAt())
                .build();
    }
}