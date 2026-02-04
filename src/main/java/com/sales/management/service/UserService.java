package com.sales.management.service;

import com.sales.management.exception.BadRequestException;
import com.sales.management.exception.ResourceNotFoundException;
import com.sales.management.model.dto.request.CreateUserRequest;
import com.sales.management.model.dto.request.UpdateUserRequest;
import com.sales.management.model.dto.response.UserResponse;
import com.sales.management.model.entity.User;
import com.sales.management.model.enums.UserRole;
import com.sales.management.repository.UserRepository;
import com.sales.management.util.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        // Validar email único
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException(Constants.EMAIL_ALREADY_EXISTS);
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .active(true)
                .build();

        user = userRepository.save(user);
        return mapToResponse(user);
    }

    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.USER_NOT_FOUND));

        // Validar email único (se estiver mudando)
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new BadRequestException(Constants.EMAIL_ALREADY_EXISTS);
            }
            user.setEmail(request.getEmail());
        }

        if (request.getName() != null) {
            user.setName(request.getName());
        }

        if (request.getActive() != null) {
            user.setActive(request.getActive());
        }

        user = userRepository.save(user);
        return mapToResponse(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.USER_NOT_FOUND));
        
        user.setActive(false);
        userRepository.save(user);
    }

    @Transactional
    public UserResponse reactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.USER_NOT_FOUND));
        
        user.setActive(true);
        user = userRepository.save(user);
        return mapToResponse(user);
    }

    public UserResponse getUserById(Long id) {
        User user = userRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.USER_NOT_FOUND));
        return mapToResponse(user);
    }

    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findByActiveTrue(pageable)
                .map(this::mapToResponse);
    }

    public Page<UserResponse> getUsersByRole(UserRole role, Pageable pageable) {
        return userRepository.findByRole(role, pageable)
                .map(this::mapToResponse);
    }

    public Page<UserResponse> searchUsers(String search, Pageable pageable) {
        return userRepository.searchUsers(search, pageable)
                .map(this::mapToResponse);
    }

    private UserResponse mapToResponse(User user) {
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